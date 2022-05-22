package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.example.retrofitrxjava.ItemBuyListener;
import com.example.retrofitrxjava.ItemDeleteCartListener;
import com.example.retrofitrxjava.ModelManager;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.CartAdt;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.ActivityCartBinding;
import com.example.retrofitrxjava.dialog.BuyBottomSheet;
import com.example.retrofitrxjava.model.Bill;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatAct<ActivityCartBinding> implements CartAdt.ListItemListener, ItemDeleteCartListener<ProductCategories>, ItemBuyListener, PaymentResultListener {
    private List<ProductCategories> productCategoriesList = new ArrayList<>();
    private CartAdt<ProductCategories> cartAdapter;
    private ModelManager objManager511;
    private ModelManager objManager3331;

    @Override
    protected void initLayout() {
        bd.setListener(this);
        bd.title.setText("Cart");
        bd.back.setOnClickListener(v -> finish());

        setupViewModel.loadManager(db, currentUser);
        getAllCart();
        initListener();
    }

    private void initListener() {
        bd.add.setOnClickListener(v -> checkOut());
        setupViewModel.getListModelManagerMutableLiveData().observe(this, modelManagers -> {
            if (modelManagers.size() > 0) {
                for (ModelManager modelManager : modelManagers) {
                    if (modelManager.getCode().equalsIgnoreCase(Constants.CODE_511)) {
                        objManager511 = modelManager;
                    }
                    if (modelManager.getCode().equalsIgnoreCase(Constants.CODE_3331)) {
                        objManager3331 = modelManager;
                    }
                }
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.activity_cart;
    }

    private void getAllCart() {
        db.collection(Constants.KEY_CART).whereEqualTo(Constants.KEY_UID, currentUser.getUid())
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_CART)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories productCategories = documentSnapshot.toObject(ProductCategories.class);
                    if (productCategories != null) {
                        productCategories.setDocumentId(documentSnapshot.getId());
                        productCategoriesList.add(productCategories);
                    }
                }

                cartAdapter = new CartAdt<>(this, R.layout.item_cart);
                bd.recycleCart.setAdapter(this.cartAdapter);
                cartAdapter.setData(productCategoriesList);
                setPriceTotal();
                cartAdapter.setListener(this);
            }
        });
    }

    public void buy() {
        Toast.makeText(this, "Mua hàng thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemBuyClick() {
        buy();
    }

    public void setPriceTotal() {
        long price = 0;
        for (ProductCategories article : productCategoriesList) {
            price += (getPrice(article.getPrice()) * article.getCount());
        }

        bd.setTotal(String.valueOf(price));
    }

    private void checkOut() {
        createBill();
        BuyBottomSheet buyBottomSheet = new BuyBottomSheet(totalPrice -> startPayment(totalPrice), Double.parseDouble(bd.getTotal()));
        buyBottomSheet.show(getSupportFragmentManager(), buyBottomSheet.getTag());
    }

    private void createBill() {
        String billId = "Bill_" + System.currentTimeMillis();
        Bill bill = new Bill();
        bill.setStatus(Constants.KEY_ITEM_PENDING);
        bill.setTime(System.currentTimeMillis());
        bill.setBillId(billId);
        bill.setTotalBill(bd.getTotal());
        bill.setUid(currentUser.getUid());
        List<String> listProductId = new ArrayList<>();
        for (ProductCategories productCategories : productCategoriesList) {
            listProductId.add(productCategories.getDocumentId());
        }
        bill.setList_id_order(listProductId);
        db.collection(Constants.KEY_BILL).document(billId).set(bill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                for (ProductCategories productCategories : productCategoriesList) {
                    productCategories.setIdBill(billId);
                    updateCartBuy(productCategories);
                }
            }
        });

        // update 511,3331
        if (objManager511 != null && objManager3331 != null) {
            long price511 = Long.parseLong(objManager511.getHave()) + Long.parseLong(bd.getTotal().replaceAll(",", ""));
            setupViewModel.updateDataManager(db, objManager511.getDocumentId(), Constants.COLLECTION_HAVE, String.valueOf(price511));
            long price3331 = Long.parseLong(objManager3331.getHave()) + (getTotal() * 10) / 100;
            setupViewModel.updateDataManager(db, objManager3331.getDocumentId(), Constants.COLLECTION_HAVE, String.valueOf(price3331));
        }
    }

    private long getTotal(){
        return Long.parseLong(bd.getTotal().replaceAll(",", ""));
    }

    public double getPrice(String price) {
        return Long.parseLong(price);
    }

    @Override
    public void onPaymentSuccess(String s) {
        createBill();
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
    }

    public void startPayment(double total) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_bvPYonKyVPrUPM");
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Merchant Name");
            options.put("description", "Payment");
            options.put("currency", "INR");
            options.put("amount", total);//pass amount in currency subunits
            options.put("prefill.email", currentUser.getEmail());
            options.put("prefill.contact", MainActivity.userModel.getPhoneNumber());

            checkout.open(this, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {

    }

    @Override
    public void onItemDeleteClick(ProductCategories productCategories, int index) {
        updateItemCount(productCategories, index, true);
    }

    @Override
    public void onItemAddClick(ProductCategories productCategories, int index) {
        updateItemCount(productCategories, index, false);
    }

    private void updateItemCount(ProductCategories productCategories, int index, boolean isDelete) {
        int count = productCategories.getCount();
        if (isDelete) {
            count--;
            if (count == 0) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setMessage("Do you want to delete item?");
                b.setPositiveButton("OK", (dialog, id) -> {
                    deleteCart(productCategories, index);
                });
                b.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
                AlertDialog al = b.create();
                al.show();
            }
        } else {
            count++;
        }
        if (count != 0) {
            productCategories.setCount(count);
            productCategoriesList.set(index, productCategories);
            setPriceTotal();
            cartAdapter.notifyItemChanged(index);
            updateCart(productCategories);
        }
        setPriceTotal();
    }

    public void updateCart(ProductCategories productCategories) {
        db.collection(Constants.KEY_CART).document(productCategories.getDocumentId())
                .update(productCategories.toMapData())
                .addOnCompleteListener(task -> {

                })
                .addOnFailureListener(e -> {

                });
    }

    public void updateCartBuy(ProductCategories productCategories) {
        productCategories.setStatus(Constants.KEY_ITEM_PENDING);
        productCategories.setTimeUpdate(System.currentTimeMillis());
        db.collection(Constants.KEY_CART).document(productCategories.getDocumentId())
                .update(productCategories.toMapData())
                .addOnCompleteListener(task -> {

                })
                .addOnFailureListener(e -> {

                });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteCart(ProductCategories productCategories, int indext) {
        db.collection(Constants.KEY_CART).document(productCategories.getDocumentId())
                .delete()
                .addOnCompleteListener(task -> {
                    productCategoriesList.remove(indext);
                    cartAdapter.notifyItemRemoved(indext);
                    cartAdapter.setData(productCategoriesList);
                    setPriceTotal();
                })
                .addOnFailureListener(e -> {

                });
    }
}
