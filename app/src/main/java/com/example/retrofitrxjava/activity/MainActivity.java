package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.paypal.pyplcheckout.pojo.To;

public class MainActivity extends AppCompatAct<ActivityMainBinding> {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initLayout() {
        loadFragment(HomeFragment.newInstance());
        setTitle("Home");
        bd.navigation.setOnNavigationItemSelectedListener(menuItem -> {
            setTitle(menuItem.getTitle());
            switch (menuItem.getItemId()) {
                case R.id.menu_movies:
                    loadFragment(HomeFragment.newInstance());
                    return true;
                case R.id.menu_cart:
                    loadFragment(CartFragment.newInstance());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.change_password:
                checkAccount();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlertDialogButtonClicked(UserModel userModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change password");
        CustomLayoutBinding customLayout = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.custom_layout, null, false);
        builder.setView(customLayout.getRoot());
        AlertDialog dialog = builder.create();
        customLayout.setItem(userModel);
        customLayout.save.setOnClickListener(v -> {
            currentUser.updatePassword(getText(customLayout.edtPasswordNew))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userModel.setPassword(getText(customLayout.edtPasswordNew));
                            db.collection("account").document(currentUser.getUid()).set(userModel);
                            Toast.makeText(MainActivity.this, "Change password success", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Change password failed", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });

        });
        dialog.show();
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void checkAccount() {
        showDialog();
        db.collection("account").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userModel = document.toObject(UserModel.class);
                        showAlertDialogButtonClicked(userModel);
                    } else {
                        Toast.makeText(MainActivity.this, "Account condition change password is not enough !", Toast.LENGTH_SHORT).show();
                    }
                    dismissDialog();
                }
                dismissDialog();
            }
        });
    }

}
