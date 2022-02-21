package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.retrofitrxjava.BuildConfig;
import com.example.retrofitrxjava.DialogRegister;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.LayoutActivityLoginViewBinding;
import com.example.retrofitrxjava.model.City;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class LoginActivity extends AppCompatAct<LayoutActivityLoginViewBinding> implements View.OnClickListener {
    private AppDatabase appDatabase;
    private DialogRegister dialogRegister;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1331;
    public static final String clientKey = "AXZ0Pd6_JmCUCzpgtOUBnnsdfsVHBvAUlX1QdY9D7d28oXMI6VyRByt27dgKWv08thEBclxNB8FTdBo9";
    public static final int PAYPAL_REQUEST_CODE = 123;
    public static UserModel userRegister = null;

    @Override
    protected void initLayout() {
        this.appDatabase = AppDatabase.getInstance(this);
        this.userModel = new UserModel();
        this.bd.setUser(userModel);

        if (currentUser != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        bd.btRegister.setOnClickListener(this);
        bd.btRequestLogin.setOnClickListener(this);
        bd.loginGoogle.setOnClickListener(v -> signIn());

        bd.loginFacebook.setOnClickListener(v -> getPayment());
    }

    private void getPayment() {
        CheckoutConfig config = new CheckoutConfig(
                getApplication(),
                clientKey,
                Environment.SANDBOX,
                BuildConfig.APPLICATION_ID, CurrencyCode.USD,
                UserAction.PAY_NOW, new SettingsConfig());
        PayPalCheckout.setConfig(config);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected int getID() {
        return R.layout.layout_activity_login_view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btRegister:
                Intent intent = new Intent(this, DialogRegister.class);
                startActivity(intent);
                break;
            case R.id.btRequestLogin:
                hideKeyboard(this);
                showDialog();
                mAuth.signInWithEmailAndPassword(getText(bd.etAccount),getText(bd.etPassword)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                    }
                    dismissDialog();
                });

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
