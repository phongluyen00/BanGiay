package com.example.retrofitrxjava.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.ItemDeleteCartListener;
import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.ItemOnclickProductListener;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.LayoutRecruitmentBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeActivity extends AppCompatAct<LayoutRecruitmentBinding> implements ItemDeleteCartListener<UserModel>, ItemOnclickListener<UserModel>, ItemOnclickProductListener<Product>, MutilAdt.ListItemListener {
    public static final String EXTRA_DATA = "extra_data";
    private MutilAdt<UserModel> adapterUser;
    private MutilAdt<Product> adapterProduct;
    private AppDatabase appDatabase;
    public static boolean isValidate = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isValidate){
            appDatabase.getStudentDao().deleteAll(userModel.getIdUser());
        }
    }

    @Override
    protected void initLayout() {
        appDatabase = AppDatabase.getInstance(this);
        Intent intent = getIntent();
        int id = intent.getIntExtra("idUser", 100);
        userModel = this.appDatabase.getStudentDao().getUserModelId(id);
        if (userModel.isAdmin()) {
            bd.rvAccount.setVisibility(View.VISIBLE);
            bd.rvRecruitment.setVisibility(View.GONE);
            adapterUser = new MutilAdt<>(HomeActivity.this, R.layout.item_account);
            bd.rvAccount.setAdapter(adapterUser);
            adapterUser.setDt((ArrayList<UserModel>) appDatabase.getStudentDao().getAllUser());
            adapterUser.setListener(this);
        } else {
            getAllData(products -> {
                adapterProduct = new MutilAdt<>(this, R.layout.item_truyen);
                adapterProduct.setDt((ArrayList<Product>) products);
                adapterProduct.setListener(this);
                bd.rvRecruitment.setAdapter(adapterProduct);
            });
        }
    }

    @Override
    protected int getID() {
        return R.layout.layout_recruitment;
    }

    @Override
    public void onItemMediaClick(UserModel userModel) {
        if (!userModel.isAdmin()) {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("idUser", userModel.getIdUser());
            intent.putExtra("permission", this.userModel.getPermission());
            startActivity(intent);
        }
    }

    @Override
    public void onItemProductClick(Product product) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_DATA, product);
        intent.putExtra("idUser", userModel.getIdUser());
        startActivity(intent);
    }

    @Override
    public void onItemDeleteClick(UserModel userModel) {
        if (userModel.isAdmin()) return;
        appDatabase.getStudentDao().deleteAccount(userModel);
        adapterUser.setDt((ArrayList<UserModel>) appDatabase.getStudentDao().getAllUser());
    }
}



