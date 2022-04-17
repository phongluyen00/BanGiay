package com.example.retrofitrxjava.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.ItemListener;
import com.example.retrofitrxjava.ItemOnclickProductListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.ActivityCategoriesBinding;
import com.example.retrofitrxjava.dialog.BaseBottomSheet;
import com.example.retrofitrxjava.dialog.BuyBottomSheet;
import com.example.retrofitrxjava.model.Categories;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.retrofitrxjava.fragment.HomeFragment.EXTRA_DATA;

public class CategoriesActivity extends AppCompatAct<ActivityCategoriesBinding> implements ItemListener<ProductCategories>, MutilAdt.ListItemListener, ItemOnclickProductListener<ProductCategories>, PaymentResultListener {

    private MutilAdt<ProductCategories> categoriesAdt;
    private List<ProductCategories> productCategoriesList = new ArrayList<>();
    private Markets markets;
    private String id_Markets = "";
    private SetupViewModel setupViewModel;

    @Override
    protected void initLayout() {
        setTitle("Categories");
        userModel = MainActivity.userModel;
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);

        if (getIntent() != null) {
            markets = (Markets) getIntent().getSerializableExtra("markets");
            assert markets != null;
            id_Markets = markets.getDocumentId();
            setupViewModel.getProductMarket(db, id_Markets);
        }
        setupViewModel.getProductCategoriesList().observe(this, productCategories -> {
            if (productCategories != null && productCategories.size() > 0) {
                productCategoriesList.addAll(productCategories);
                loadData(productCategories);
            }
        });

        setupViewModel.getDeleteProduct().observe(this, new Observer<List<ProductCategories>>() {
            @Override
            public void onChanged(List<ProductCategories> productCategoriesList) {
                dismissDialog();
                if (productCategoriesList != null){
                    categoriesAdt.setDt((ArrayList<ProductCategories>) productCategoriesList);
                    Toast.makeText(CategoriesActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                }
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
        BaseBottomSheet baseBottomSheet = new BuyBottomSheet(new BuyBottomSheet.itemListener() {
            @Override
            public void onSubmit(double totalPrice) {
                startPayment(totalPrice);
            }
        }, Double.parseDouble(productCategories.getPrice()));
        baseBottomSheet.show(getSupportFragmentManager(), baseBottomSheet.getTag());
    }

    private void startPayment(double totalPrice) {
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
            options.put("amount", totalPrice * 0.90);//pass amount in currency subunits
            options.put("prefill.email", currentUser.getEmail());
            options.put("prefill.contact", MainActivity.userModel.getPhoneNumber());

            checkout.open(this, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    private void loadData(List<ProductCategories> productCategories) {
        categoriesAdt = new MutilAdt<>(this, userModel.getPermission() == 1 ? R.layout.item_product_admin : R.layout.item_product);
        categoriesAdt.setListener(this);
        bd.rclCategories.setAdapter(categoriesAdt);
        categoriesAdt.setDt((ArrayList<ProductCategories>) productCategories);
    }

    @Override
    public void onPaymentSuccess(String s) {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPaymentError(int i, String s) {

    }

    @Override
    public void onEditProduct(ProductCategories productCategories, int position) {
        Intent intent = new Intent(this, ActivityAdd.class);
        intent.putExtra(EXTRA_DATA, productCategories);
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(ProductCategories productCategories, int position) {
        showDialog();
        setupViewModel.deleteProduct(this, db, productCategories, productCategoriesList);
    }

    @Override
    public void onClickProduct(ProductCategories productCategories) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_DATA, productCategories);
        startActivity(intent);
    }

    @Override
    public void onAddProduct(ProductCategories productCategories) {
        long quantity = Long.parseLong(productCategories.getQuantity()) + 1;
        productCategories.setQuantity(String.valueOf(quantity));
        db.collection("product_markets").document(productCategories.getDocumentId()).set(productCategories);
        Toast.makeText(this, "Thêm số lượng thành công", Toast.LENGTH_SHORT).show();
    }
}
