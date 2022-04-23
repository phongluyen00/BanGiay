package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.retrofitrxjava.DialogRegister;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.LayoutActivityLoginViewBinding;
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

import org.jsoup.helper.StringUtil;

public class LoginActivity extends AppCompatAct<LayoutActivityLoginViewBinding> implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1331;
    public static final String clientKey = "AXZ0Pd6_JmCUCzpgtOUBnnsdfsVHBvAUlX1QdY9D7d28oXMI6VyRByt27dgKWv08thEBclxNB8FTdBo9";
    public static final int PAYPAL_REQUEST_CODE = 123;

    @Override
    protected void initLayout() {
        this.appDatabase = AppDatabase.getInstance(this);
        this.userModel = new UserModel();
        this.bd.setUser(userModel);

        if (currentUser != null) {
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

//        bd.loginFacebook.setOnClickListener(v -> getPayment());
        bd.forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtil.isBlank(getText(bd.etAccount))){
                    mAuth.sendPasswordResetEmail(getText(bd.etAccount))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Send password resetEmail success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Send password resetEmail failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(LoginActivity.this, "Nhập email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPayment() {

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
                if (StringUtil.isBlank(getText(bd.etAccount)) || StringUtil.isBlank(getText(bd.etPassword)) || getText(bd.etPassword).length() < 7) {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog();
                mAuth.signInWithEmailAndPassword(getText(bd.etAccount), getText(bd.etPassword)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
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
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
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
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
