package com.example.retrofitrxjava.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.retrofitrxjava.ItemOnclickProductListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.ActivityCategoriesBinding;
import com.example.retrofitrxjava.dialog.BaseBottomSheet;
import com.example.retrofitrxjava.dialog.BuyBottomSheet;
import com.example.retrofitrxjava.model.Categories;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.retrofitrxjava.fragment.HomeFragment.EXTRA_DATA;

public class CategoriesActivity extends AppCompatAct<ActivityCategoriesBinding> implements MutilAdt.ListItemListener, ItemOnclickProductListener<ProductCategories> , PaymentResultListener {

    private MutilAdt<ProductCategories> categoriesAdt;
    private List<ProductCategories> productCategoriesList = new ArrayList<>();
    private Markets markets;
    private String id_Markets = "";

    @Override
    protected void initLayout() {
        setTitle("Categories");
        if (getIntent() != null) {
            markets = (Markets) getIntent().getSerializableExtra("markets");
            id_Markets = markets.getDocumentId();
        }
        db.collection("product_markets").whereEqualTo("id_markets", id_Markets).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories product = documentSnapshot.toObject(ProductCategories.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productCategoriesList.add(product);
                    }
                }
                categoriesAdt = new MutilAdt<>(this, R.layout.item_product);
                categoriesAdt.setListener(this);
                bd.rclCategories.setAdapter(categoriesAdt);
                categoriesAdt.setDt((ArrayList<ProductCategories>) productCategoriesList);
            }
        });

        bd.title.setText(markets.getTitle());
        bd.back.setOnClickListener(v -> finish());
    }

    @Override
    protected int getID() {
        return R.layout.activity_categories;
    }

    @Override
    public void onItemProductClick(ProductCategories productCategories) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_DATA, productCategories);
//        intent.putExtra("idUser", userModel.getIdUser());
        startActivity(intent);
    }

    @Override
    public void onItemAddListener(ProductCategories productCategories) {
        BaseBottomSheet baseBottomSheet = new BuyBottomSheet(() -> startPayment(), userModel, 2000);
        baseBottomSheet.show(getSupportFragmentManager(),baseBottomSheet.getTag());
    }

    private void startPayment() {
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
    public void onPaymentSuccess(String s) {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPaymentError(int i, String s) {

    }
}
