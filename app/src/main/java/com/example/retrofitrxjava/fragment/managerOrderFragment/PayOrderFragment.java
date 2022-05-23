package com.example.retrofitrxjava.fragment.managerOrderFragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.retrofitrxjava.ItemFavoriteListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.adapter.PayOrderAdapter;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.FragmentPayOrderBinding;
import com.example.retrofitrxjava.event.UpdateMain;
import com.example.retrofitrxjava.fragment.BaseFragment;
import com.example.retrofitrxjava.model.Bill;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.firestore.DocumentSnapshot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PayOrderFragment extends BaseFragment<FragmentPayOrderBinding> implements MutilAdt.ListItemListener, ItemFavoriteListener<ProductCategories>, PayOrderAdapter.ListItemListener {

    private PayOrderAdapter adapter;
    private List<Bill> listBills;
    private boolean isTypeAdmin = false;

    public static PayOrderFragment newInstance(Boolean isTypeAdmin) {
        Bundle args = new Bundle();
        args.putBoolean(Constants.ARG_TYPE_ADMIN, isTypeAdmin);
        PayOrderFragment fragment = new PayOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTypeAdmin = getArguments().getBoolean(Constants.ARG_TYPE_ADMIN);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initAdapter() {
        listBills = new ArrayList<>();
        adapter = new PayOrderAdapter(getContext());
        adapter.setCallback(this);
        adapter.setTypeAdmin(isTypeAdmin);
        binding.recycleCart.setAdapter(adapter);
        adapter.setShowCancel(true);
        if (isTypeAdmin) {
            getAllBillAdmin();
        } else {
            getAllBill();
        }
    }

    @Override
    protected void initLiveData() {

    }

    @Override
    protected void initFragment() {
    }

    @Override
    protected int getID() {
        return R.layout.fragment_pay_order;
    }

    @Override
    public void onClickProduct(ProductCategories productCategories) {

    }

    @Override
    public void onClickRemove(ProductCategories productCategories) {

    }

    private void getAllCart() {
        db.collection(Constants.KEY_CART).whereEqualTo(Constants.KEY_UID, currentUser.getUid())
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_PENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductCategories> listProductCategories = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories productCategories = documentSnapshot.toObject(ProductCategories.class);
                    if (productCategories != null) {
                        productCategories.setDocumentId(documentSnapshot.getId());
                        listProductCategories.add(productCategories);
                    }
                }
                List<Bill> listBillNew = new ArrayList<>();
                for (int i = 0; i < listBills.size(); i++) {
                    listBillNew.add(getListCategoriesBill(listBills.get(i), listProductCategories));
                }
                adapter.setList(listBillNew);
            }
        });
    }

    private void getAllBill() {
        db.collection(Constants.KEY_BILL)
                .whereEqualTo(Constants.KEY_UID, currentUser.getUid())
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_PENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                    Bill productCategories = task.getResult().getDocuments().get(i).toObject(Bill.class);
                    productCategories.setBillId(task.getResult().getDocuments().get(i).getId());
                    listBills.add(productCategories);
                }
                getAllCart();
            }
        }).addOnFailureListener(e -> {

        });
    }

    private void getAllCartAdmin() {
        db.collection(Constants.KEY_CART)
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_PENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductCategories> listProductCategories = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories productCategories = documentSnapshot.toObject(ProductCategories.class);
                    if (productCategories != null) {
                        productCategories.setDocumentId(documentSnapshot.getId());
                        listProductCategories.add(productCategories);
                    }
                }

                List<Bill> listBillNew = new ArrayList<>();
                for (int i = 0; i < listBills.size(); i++) {
                    listBillNew.add(getListCategoriesBill(listBills.get(i), listProductCategories));
                }
                adapter.setList(listBillNew);
            }
        });
    }

    private Bill getListCategoriesBill(Bill bill, List<ProductCategories> listProductCategories) {
        List<ProductCategories> listProductCategoriesNew = new ArrayList<>();
        for (ProductCategories listProductCategory : listProductCategories) {
            if (listProductCategory.getIdBill().equals(bill.getBillId())) {
                listProductCategoriesNew.add(listProductCategory);
            }
        }
        bill.setProductCategoriesList(listProductCategoriesNew);
        return bill;
    }

    private void getAllBillAdmin() {
        db.collection(Constants.KEY_BILL)
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_PENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                    Bill productCategories = task.getResult().getDocuments().get(i).toObject(Bill.class);
                    productCategories.setBillId(task.getResult().getDocuments().get(i).getId());
                    listBills.add(productCategories);
                }
                getAllCartAdmin();
            }
        }).addOnFailureListener(e -> {

        });
    }


    public void updateBill(Bill bill, int position, boolean isClickCancel) {
        db.collection(Constants.KEY_BILL).document(bill.getBillId())
                .update(bill.toMapData())
                .addOnCompleteListener(task -> {
                    adapter.removeItem(position);
                    for (ProductCategories productCategories : bill.getProductCategoriesList()) {
                        updateCartBuy(productCategories, isClickCancel);
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    public void updateCartBuy(ProductCategories productCategories, boolean isClickCancel) {
        productCategories.setStatus(!isClickCancel ? Constants.KEY_ITEM_RECEIVE : Constants.KEY_ITEM_CANCEL);
        db.collection(Constants.KEY_CART).document(productCategories.getDocumentId())
                .update(productCategories.toMapData())
                .addOnCompleteListener(task -> {
                    EventBus.getDefault().post(new UpdateMain(isClickCancel ? "Cancel" : "Accept PayOrder"));
                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    public void clickCancelOrder(Bill item, int position) {
        item.setStatus(Constants.KEY_ITEM_CANCEL);
        item.setTimeUpdate(System.currentTimeMillis());
        updateBill(item, position, true);
    }

    @Override
    public void clickItemAcceptOrder(Bill item, int position) {
        item.setStatus(Constants.KEY_ITEM_RECEIVE);
        item.setTimeUpdate(System.currentTimeMillis());
        updateBill(item, position, false);
    }
}
