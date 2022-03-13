package com.example.retrofitrxjava.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.retrofitrxjava.ItemBuyListener;
import com.example.retrofitrxjava.ItemDeleteCartListener;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.AppCompatAct;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.activity.PaymentSuccessActivity;
import com.example.retrofitrxjava.adapter.CartAdt;
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

public class CartFragment extends AppCompatAct<ActivityCartBinding> implements CartAdt.ListItemListener, ItemDeleteCartListener, ItemBuyListener, PaymentResultListener {
    private AppDatabase appDatabase;
    private List<ProductCategories> productCategoriesList = new ArrayList<>();
    private CartAdt<ProductCategories> cartAdapter;

    @Override
    protected void initLayout() {
        this.appDatabase = AppDatabase.getInstance(this);
        bd.setListener(this);

        bd.title.setText("Cart");
        bd.back.setOnClickListener(v -> finish());
        db.collection("cart").whereEqualTo("uid", currentUser.getUid()).get().addOnCompleteListener(task -> {
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

        bd.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOut();
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.activity_cart;
    }

    @Override
    public void onItemDeleteClick(Object o) {
        Product article = (Product) o;
        if (article.getCount() <= 1) {
            appDatabase.getStudentDao().delete((Product) o);
        } else {
            article.setCount(article.getCount() - 1);
            appDatabase.getStudentDao().update(article);
        }
//        this.listArticle = appDatabase.getStudentDao().getAll(userModel.getIdUser());
//        cartAdapter.setData(listArticle);
        setPriceTotal();
        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
    }

    public void buy() {
//        appDatabase.getStudentDao().deleteAll(userModel.getIdUser());
//        this.listArticle = this.appDatabase.getStudentDao().getAll(userModel.getIdUser());
//        cartAdapter.setData(listArticle);
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
//        for (Product article : listArticle) {
//            price += getPrice(article.getPrice()) * article.getCount();
//        }
        bd.setTotal(String.valueOf(price));
    }

    private void checkOut() {
        BuyBottomSheet buyBottomSheet = new BuyBottomSheet(this::startPayment,userModel,15454);
        buyBottomSheet.show(getSupportFragmentManager(), buyBottomSheet.getTag());
    }

    public double getPrice(String price) {
        return Double.parseDouble(price);
    }

    @Override
    public void onPaymentSuccess(String s) {
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
        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {

    }
}
