package com.example.retrofitrxjava;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.retrofitrxjava.activity.AppCompatAct;
import com.example.retrofitrxjava.activity.LoginActivity;
import com.example.retrofitrxjava.databinding.DialogRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class DialogRegister extends AppCompatAct<DialogRegisterBinding> {

    private UserModel userModel;

    @Override
    protected void initLayout() {
        userModel = new UserModel();
        bd.setUser(userModel);
        bd.register.setOnClickListener(v -> {
            if (getText(bd.etPassword).length() < 7) {
                Toast.makeText(this, "Password must be greater than or equal to 7 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            showDialog();
            userModel = new UserModel(getText(bd.tvName), getText(bd.edtEmail), getText(bd.etPassword), getText(bd.edtPhone), getText(bd.edtAddress),"1000");
            createAccount(userModel);
        });

        bd.tvCancel.setOnClickListener(v -> finish());
    }

    private void createAccount(UserModel userModel) {
        hideKeyboard(this);
        mAuth.createUserWithEmailAndPassword(userModel.getEmail(), userModel.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String id = task.getResult().getUser().getUid();
                        db.collection("account").document(id).set(userModel);
                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Đăng ký không thành công" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    dismissDialog();
                });
    }

    @Override
    protected int getID() {
        return R.layout.dialog_register;
    }

}
