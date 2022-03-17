package com.example.retrofitrxjava.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.retrofitrxjava.ItemBuyListener;
import com.example.retrofitrxjava.ItemDeleteCartListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.CartAdt;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.ActivityCartBinding;
import com.example.retrofitrxjava.dialog.BuyBottomSheet;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatAct<ActivityCartBinding> implements CartAdt.ListItemListener, ItemDeleteCartListener<ProductCategories>, ItemBuyListener, PaymentResultListener {
    private AppDatabase appDatabase;
    private List<ProductCategories> productCategoriesList = new ArrayList<>();
    private CartAdt<ProductCategories> cartAdapter;

    @Override
    protected void initLayout() {
        this.appDatabase = AppDatabase.getInstance(this);
        bd.setListener(this);
        bd.title.setText("Cart");
        bd.back.setOnClickListener(v -> finish());
        getAllCart();
        initListener();
    }

    private void initListener() {
        bd.add.setOnClickListener(v -> checkOut());
    }

    @Override
    protected int getID() {
        return R.layout.activity_cart;
    }

    private void getAllCart() {
        db.collection(Constants.KEY_CART).whereEqualTo(Constants.KEY_UID, currentUser.getUid()).get().addOnCompleteListener(task -> {
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
        double price = 0;
        for (ProductCategories article : productCategoriesList) {
            price += getPrice(article.getPrice()) * article.getCount();
        }

        bd.setTotal(String.valueOf(price));
    }

    private void checkOut() {
        BuyBottomSheet buyBottomSheet = new BuyBottomSheet(this::startPayment, userModel, 15454);
        buyBottomSheet.show(getSupportFragmentManager(), buyBottomSheet.getTag());
    }

    public double getPrice(String price) {
        return Double.parseDouble(price);
    }

    @Override
    public void onPaymentSuccess(String s) {
        for (ProductCategories productCategories : productCategoriesList) {
            updateCartBuy(productCategories);
        }
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
    }

    public void startPayment() {
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
            options.put("amount", "300");//pass amount in currency subunits
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
        db.collection(Constants.KEY_CART).document(productCategories.getDocumentId())
                .update(Constants.KEY_STATUS, Constants.KEY_ITEM_PENDING)
                .addOnCompleteListener(task -> {

                })
                .addOnFailureListener(e -> {

                });
    }

    public void deleteCart(ProductCategories productCategories, int indext) {
        db.collection(Constants.KEY_CART).document(productCategories.getDocumentId())
                .delete()
                .addOnCompleteListener(task -> {
                    productCategoriesList.remove(indext);
                    cartAdapter.notifyItemRemoved(indext);
                })
                .addOnFailureListener(e -> {

                });
    }
}
