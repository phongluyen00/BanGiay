package com.example.retrofitrxjava.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import com.example.retrofitrxjava.ItemBuyListener;
import com.example.retrofitrxjava.ItemDeleteCartListener;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.PaymentSuccessActivity;
import com.example.retrofitrxjava.adapter.CartAdt;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.ActivityCartBinding;
import com.example.retrofitrxjava.fragment.BaseFragment;
import com.example.retrofitrxjava.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends BaseFragment<ActivityCartBinding> implements CartAdt.ListItemListener, ItemDeleteCartListener, ItemBuyListener {
    private AppDatabase appDatabase;
    private List<Product> listArticle;
    private CartAdt<Product> cartAdapter;

    public static CartFragment newInstance() {

        Bundle args = new Bundle();
        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        Intent intent = activity.getIntent();
        int id = intent.getIntExtra("idUser", 0);
//        userModel = this.appDatabase.getStudentDao().getUserModelId(id);
        this.listArticle = new ArrayList<>();
//        this.listArticle = this.appDatabase.getStudentDao().getAll(userModel.getIdUser());
        cartAdapter = new CartAdt<>(activity, R.layout.item_cart);
        binding.recycleCart.setAdapter(this.cartAdapter);
        cartAdapter.setData(listArticle);
        setPriceTotal();
        cartAdapter.setListener(this);
    }

    @Override
    protected void initLiveData() {

    }

    @Override
    protected void initFragment() {
        this.appDatabase = AppDatabase.getInstance(activity);
        binding.setListener(this);
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
        cartAdapter.setData(listArticle);
        setPriceTotal();
        Toast.makeText(activity, "delete", Toast.LENGTH_SHORT).show();
    }

    public void buy() {
//        appDatabase.getStudentDao().deleteAll(userModel.getIdUser());
//        this.listArticle = this.appDatabase.getStudentDao().getAll(userModel.getIdUser());
        cartAdapter.setData(listArticle);
        Toast.makeText(activity, "Mua hàng thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, PaymentSuccessActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemBuyClick() {
        buy();
    }

    public void setPriceTotal() {
        double price = 0;
        for (Product article : listArticle) {
            price += getPrice(article.getPrice()) * article.getCount();
        }
        binding.setTotal(String.valueOf(price));
    }


    public double getPrice(String price) {
        return Double.parseDouble(price);
    }
}
