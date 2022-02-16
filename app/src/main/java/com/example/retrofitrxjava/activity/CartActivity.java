package com.example.retrofitrxjava.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

import com.example.retrofitrxjava.ItemBuyListener;
import com.example.retrofitrxjava.ItemDeleteCartListener;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.CartAdt;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.ActivityCartBinding;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatAct<ActivityCartBinding> implements CartAdt.ListItemListener, ItemDeleteCartListener, ItemBuyListener {
    private AppDatabase appDatabase;
    private List<Product> listArticle;
    private CartAdt<Product> cartAdapter;

    @Override
    protected void initLayout() {
        this.appDatabase = AppDatabase.getInstance(this);
        Intent intent = getIntent();
        int id = intent.getIntExtra("idUser", 0);
        int permission = intent.getIntExtra("permission", 100);
        userModel = this.appDatabase.getStudentDao().getUserModelId(id);
        this.listArticle = new ArrayList<>();
        this.listArticle = this.appDatabase.getStudentDao().getAll(userModel.getIdUser());
        cartAdapter = new CartAdt<>(CartActivity.this, R.layout.item_cart);
        bd.recycleCart.setAdapter(this.cartAdapter);
        cartAdapter.setData(listArticle);
        setPriceTotal();
        bd.setListener(this);
        cartAdapter.setListener(this);
        bd.add.setVisibility(listArticle.isEmpty() || permission == 0 ? View.GONE : View.VISIBLE);
        bd.add.setOnClickListener(v -> {
            biometricPrompt.authenticate(promptInfo);
        });
        biometricPrompt = new BiometricPrompt(CartActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                HomeActivity.isValidate = true;
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder().
                setTitle("Biometric login for my app").
                setSubtitle("Log in using your biometric credential").
                setNegativeButtonText("Use account password").
                build();
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
        this.listArticle = this.appDatabase.getStudentDao().getAll(userModel.getIdUser());
        cartAdapter.setData(listArticle);
        setPriceTotal();
        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
    }

    public void buy() {
        appDatabase.getStudentDao().deleteAll(userModel.getIdUser());
        this.listArticle = this.appDatabase.getStudentDao().getAll(userModel.getIdUser());
        cartAdapter.setData(listArticle);
        Toast.makeText(this, "Mua hàng thành công!", Toast.LENGTH_SHORT).show();
        bd.tvTotal.setText("0đ");
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
        bd.setTotal(String.valueOf(price));
    }


    public double getPrice(String price) {
        return Double.parseDouble(price);
    }
}
