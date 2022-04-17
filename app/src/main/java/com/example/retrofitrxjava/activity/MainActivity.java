package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.databinding.ActivityMainBinding;
import com.example.retrofitrxjava.fragment.AccountFragment;
import com.example.retrofitrxjava.fragment.FavoriteFragment;
import com.example.retrofitrxjava.fragment.HomeFragment;
import com.example.retrofitrxjava.fragment.managerOrderFragment.ManageOrderFragment;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;
import com.razorpay.PaymentResultListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatAct<ActivityMainBinding> implements PaymentResultListener {

    public static UserModel userModel;
    private SetupViewModel setupViewModel;
    public static ArrayList<ProductCategories> productCategoriesList = new ArrayList<>();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initLayout() {
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        setupViewModel.loadAccount(db, currentUser);

        loadFragment(HomeFragment.newInstance());
        setTitle("Home");
        bd.card.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });
        bd.navigation.setOnNavigationItemSelectedListener(menuItem -> {
            setTitle(menuItem.getTitle());
            switch (menuItem.getItemId()) {
                case R.id.menu_movies:
                    loadFragment(HomeFragment.newInstance());
                    return true;
                case R.id.bill:
                    loadFragment(ManageOrderFragment.newInstance(false));
                    return true;
                case R.id.menu_favorite:
                    loadFragment(FavoriteFragment.newInstance());
                    return true;
                case R.id.menu_account:
                    loadFragment(AccountFragment.newInstance());
                    return true;
            }
            return false;
        });
    }

    @Override
    protected int getID() {
        return R.layout.activity_main;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setTitle(String title) {
        bd.title.setText(title);
//        bd.add.setVisibility(View.VISIBLE);
        bd.card.setVisibility(View.VISIBLE);
        bd.title.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        bd.title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void setViewToolbarFragmentManager(String title) {
        bd.title.setText(title);
        bd.title.setTextColor(ContextCompat.getColor(this, R.color.black));
        Drawable img = getResources().getDrawable(R.drawable.ic_back_black);
        img.setBounds(120, 0, 120, 0);
        bd.title.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
//        bd.add.setVisibility(View.GONE);
        bd.card.setVisibility(View.GONE);
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
