package com.example.retrofitrxjava.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.ItemListener;
import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.ItemOnclickProductListener;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.ActivityAdd;
import com.example.retrofitrxjava.activity.CategoriesActivity;
import com.example.retrofitrxjava.activity.DetailActivity;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.activity.MainActivityAdmin;
import com.example.retrofitrxjava.adapter.BannerAdapter;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.LayoutRecruitmentBinding;
import com.example.retrofitrxjava.dialog.BaseBottomSheet;
import com.example.retrofitrxjava.dialog.BuyBottomSheet;
import com.example.retrofitrxjava.event.UpdateMain;
import com.example.retrofitrxjava.model.Banner;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.razorpay.Checkout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends BaseFragment<LayoutRecruitmentBinding> implements ItemOnclickProductListener<ProductCategories>, ItemOnclickListener<Markets>, MutilAdt.ListItemListener, ItemListener<ProductCategories> {

    public static final String EXTRA_DATA = "extra_data";
    private MutilAdt<Markets> adapterProduct;
    private AppDatabase appDatabase;
    private MutilAdt<ProductCategories> categoriesAdt;
    private SetupViewModel setupViewModel;
    private List<ProductCategories> categories = new ArrayList<>();

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        EventBus.getDefault().register(this);
        userModel = MainActivity.userModel;
        setupViewModel.getMarket(db);
        setupViewModel.getMarketsLiveData().observe(this, markets -> {
            if (markets != null && markets.size() > 0) {
                showListMarkets(markets);
            }
        });
    }

    private void showListMarkets(List<Markets> markets) {
        adapterProduct = new MutilAdt<>(activity, R.layout.item_account);
        adapterProduct.setDt((ArrayList<Markets>) markets);
        adapterProduct.setListener(this);
        binding.rclMarket.setAdapter(adapterProduct);
        dismissDialog();
    }

    @Override
    protected void initLiveData() {
        setupViewModel.getDeleteProduct().observe(this, new Observer<List<ProductCategories>>() {
            @Override
            public void onChanged(List<ProductCategories> productCategoriesList) {
                dismissDialog();
                if (productCategoriesList != null) {
                    categoriesAdt.setDt((ArrayList<ProductCategories>) productCategoriesList);
                    Toast.makeText(activity, "Xóa thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void initFragment() {
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        appDatabase = AppDatabase.getInstance(getActivity());
        Intent intent = activity.getIntent();
        showDialog();
        setupViewModel.getProductMarketHome(db);
        setupViewModel.getProductMarkets().observe(this, productCategories -> showProductMarkets(productCategories));

        db.collection("banner").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Banner> banners = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Banner banner = documentSnapshot.toObject(Banner.class);
                    if (banner != null) {
//                        product.setDocumentId(documentSnapshot.getId());
                        banners.add(banner);
                    }
                }
                BannerAdapter adapter1 = new BannerAdapter(activity, (ArrayList) banners, R.layout.item_banner);
                binding.viewpager.setAdapter(adapter1);
            }
        });

    }

    private void showProductMarkets(ArrayList<ProductCategories> productCategoriesList) {
        categoriesAdt = new MutilAdt<>(activity, MainActivity.userModel.getPermission() == 1 ? R.layout.item_product_admin : R.layout.item_product);
        categoriesAdt.setListener(this);
        binding.rvRecruitment.setAdapter(categoriesAdt);
        categories.clear();
        categories.addAll(productCategoriesList);
        categoriesAdt.setDt(productCategoriesList);
    }

    @Override
    protected int getID() {
        return R.layout.layout_recruitment;
    }

    @Override
    public void onItemProductClick(ProductCategories product) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(EXTRA_DATA, product);
//        intent.putExtra("idUser", userModel.getIdUser());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateMain event) {
        // Do something
        if (event != null) {
            setupViewModel.getProductMarketHome(db);
        }
    }

    @Override
    public void onItemAddListener(ProductCategories product) {
        BaseBottomSheet baseBottomSheet = new BuyBottomSheet(this::startPayment, Double.parseDouble(product.getPrice()));
        baseBottomSheet.show(getChildFragmentManager(), baseBottomSheet.getTag());
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

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemMediaClick(Markets markets) {
        Intent intent = new Intent(getActivity(), CategoriesActivity.class);
        intent.putExtra("markets", markets);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onEditProduct(ProductCategories productCategories, int position) {
        Intent intent = new Intent(activity, ActivityAdd.class);
        intent.putExtra(EXTRA_DATA, productCategories);
        intent.putExtra("edit", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(ProductCategories productCategories, int position) {
        Toast.makeText(activity, "onDeleteProduct" + productCategories.getDocumentId(), Toast.LENGTH_SHORT).show();
        showDialog();
        setupViewModel.deleteProduct(activity, db, productCategories, categories);
    }

    @Override
    public void onClickProduct(ProductCategories productCategories) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(EXTRA_DATA, productCategories);
        startActivity(intent);
    }
}



