package com.example.retrofitrxjava.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.ItemOnclickProductListener;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.CategoriesActivity;
import com.example.retrofitrxjava.activity.DetailActivity;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.LayoutRecruitmentBinding;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment<LayoutRecruitmentBinding> implements ItemOnclickProductListener<ProductCategories>, ItemOnclickListener<Markets>, MutilAdt.ListItemListener {

    public static final String EXTRA_DATA = "extra_data";
    private MutilAdt<Markets> adapterProduct;
    private AppDatabase appDatabase;
    private MutilAdt<ProductCategories> categoriesAdt;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        getAllData(products -> {
            adapterProduct = new MutilAdt<>(activity, R.layout.item_account);
            adapterProduct.setDt((ArrayList<Markets>) products);
            adapterProduct.setListener(this);
            binding.rclMarket.setAdapter(adapterProduct);
            dismissDialog();
        });
    }

    @Override
    protected void initLiveData() {

    }

    @Override
    protected void initFragment() {
        appDatabase = AppDatabase.getInstance(getActivity());
        Intent intent = activity.getIntent();
        int id = intent.getIntExtra("idUser", 100);
        showDialog();
        ArrayList<ProductCategories> productCategoriesList = new ArrayList<>();
        db.collection("product_categories").whereEqualTo("type", "home_new").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories product = documentSnapshot.toObject(ProductCategories.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productCategoriesList.add(product);
                    }
                }
                categoriesAdt = new MutilAdt<>(activity,R.layout.item_product);
                categoriesAdt.setListener(this);
                binding.rvRecruitment.setAdapter(categoriesAdt);
                categoriesAdt.setDt(productCategoriesList);
            }
        });
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

    @Override
    public void onItemAddListener(ProductCategories product) {
//        List<Product> all = appDatabase.getStudentDao().getAll(userModel.getIdUser());
//        if (all != null) {
//            for (Product article1 : appDatabase.getStudentDao().getAll(userModel.getIdUser())) {
//                if (TextUtils.equals(article1.getName(), product.getTitle())) {
//                    article1.setCount(article1.getCount() + 1);
//                    appDatabase.getStudentDao().update(article1);
//                    Toast.makeText(activity, "Thêm giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        }
//        product.setCount(1);
//        product.setIdUserModel(userModel.getIdUser());
//        appDatabase.getStudentDao().insertProduct(product);
        Toast.makeText(activity, product.getPrice(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemMediaClick(Markets markets) {
        Intent intent = new Intent(getActivity(), CategoriesActivity.class);
        intent.putExtra("status", markets.getStatus());
        startActivity(intent);
    }
}



