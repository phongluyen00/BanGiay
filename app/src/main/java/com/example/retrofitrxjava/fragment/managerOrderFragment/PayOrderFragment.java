package com.example.retrofitrxjava.fragment.managerOrderFragment;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.retrofitrxjava.ItemFavoriteListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.adapter.PayOrderAdapter;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.FragmentPayOrderBinding;
import com.example.retrofitrxjava.fragment.BaseFragment;
import com.example.retrofitrxjava.model.Bill;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.firestore.DocumentSnapshot;

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
        Log.i("hadtt", "onResume: ");
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

    private void getAllCart(String idBill, int position) {
        db.collection(Constants.KEY_CART).whereEqualTo(Constants.KEY_UID, currentUser.getUid())
                .whereEqualTo(Constants.KEY_ID_BILL, idBill)
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
                Bill bill = listBills.get(position);
                bill.setProductCategoriesList(listProductCategories);
                listBills.set(position, bill);
                adapter.setList(listBills);
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
                    getAllCart(task.getResult().getDocuments().get(i).getId(), i);
                }
            }
        }).addOnFailureListener(e -> {

        });
    }

    private void getAllCartAdmin(String idBill, int position) {
        db.collection(Constants.KEY_CART)
                .whereEqualTo(Constants.KEY_ID_BILL, idBill)
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
                Bill bill = listBills.get(position);
                bill.setProductCategoriesList(listProductCategories);
                listBills.set(position, bill);
                adapter.setList(listBills);
            }
        });
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
                    getAllCartAdmin(task.getResult().getDocuments().get(i).getId(), i);
                }
            }
        }).addOnFailureListener(e -> {

        });
    }


    public void updateBill(Bill bill, int position) {
        db.collection(Constants.KEY_BILL).document(bill.getBillId())
                .update(bill.toMapData())
                .addOnCompleteListener(task -> {
                    adapter.removeItem(position);
                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    public void clickCancelOrder(Bill item, int position) {
        item.setStatus(Constants.KEY_ITEM_CANCEL);
        item.setTimeUpdate(System.currentTimeMillis());
        updateBill(item, position);
    }

    @Override
    public void clickItemAcceptOrder(Bill item, int position) {
        item.setStatus(Constants.KEY_ITEM_RECEIVE);
        item.setTimeUpdate(System.currentTimeMillis());
        updateBill(item, position);
    }
}
