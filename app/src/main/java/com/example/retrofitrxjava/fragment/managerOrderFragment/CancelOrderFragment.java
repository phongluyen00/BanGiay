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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CancelOrderFragment extends BaseFragment<FragmentPayOrderBinding> implements MutilAdt.ListItemListener, ItemFavoriteListener<ProductCategories> {

    private PayOrderAdapter adapter;
    private Boolean isTypeAdmin = false;

    public static CancelOrderFragment newInstance(Boolean isTypeAdmin) {
        Bundle args = new Bundle();
        args.putBoolean(Constants.ARG_TYPE_ADMIN, isTypeAdmin);
        CancelOrderFragment fragment = new CancelOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isTypeAdmin = getArguments().getBoolean(Constants.ARG_TYPE_ADMIN);
    }

    @Override
    protected void initAdapter() {
        adapter = new PayOrderAdapter(getContext());
        binding.recycleCart.setAdapter(adapter);
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

    private void getAllCart(ArrayList<Bill> listBills) {
        db.collection(Constants.KEY_CART).whereEqualTo(Constants.KEY_UID, currentUser.getUid())
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_CANCEL)
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
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_CANCEL)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Bill> listBills = new ArrayList<>();
                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                    Bill productCategories = task.getResult().getDocuments().get(i).toObject(Bill.class);
                    productCategories.setBillId(task.getResult().getDocuments().get(i).getId());
                    listBills.add(productCategories);
                }
                getAllCart(listBills);
            }
        }).addOnFailureListener(e -> {

        });
    }


    private void getAllBillAdmin() {
        db.collection(Constants.KEY_BILL)
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_CANCEL)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Bill> listBills = new ArrayList<>();

                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                    Bill productCategories = task.getResult().getDocuments().get(i).toObject(Bill.class);
                    productCategories.setBillId(task.getResult().getDocuments().get(i).getId());
                    listBills.add(productCategories);
                }
                getAllCartAdmin(listBills);
            }
        }).addOnFailureListener(e -> {

        });
    }

    private void getAllCartAdmin(ArrayList<Bill> listBills) {
        db.collection(Constants.KEY_CART)
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_CANCEL)
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateMain event) {
        // Do something
        if (event != null && event.getMessage().equals("Cancel")) {
            getAllBillAdmin();
        }
    }
}
