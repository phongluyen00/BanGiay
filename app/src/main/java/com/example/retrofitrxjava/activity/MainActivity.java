package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.databinding.ActivityMainBinding;
import com.example.retrofitrxjava.databinding.CustomLayoutBinding;
import com.example.retrofitrxjava.fragment.AccountFragment;
import com.example.retrofitrxjava.fragment.CartFragment;
import com.example.retrofitrxjava.fragment.FavoriteFragment;
import com.example.retrofitrxjava.fragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jsoup.helper.StringUtil;

public class MainActivity extends AppCompatAct<ActivityMainBinding> {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initLayout() {
        loadFragment(HomeFragment.newInstance());
        setTitle("Home");
        bd.card.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartFragment.class);
            startActivity(intent);
        });
        bd.navigation.setOnNavigationItemSelectedListener(menuItem -> {
            setTitle(menuItem.getTitle());
            switch (menuItem.getItemId()) {
                case R.id.menu_movies:
                    loadFragment(HomeFragment.newInstance());
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
    }
}
