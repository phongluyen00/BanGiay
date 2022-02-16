package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.retrofitrxjava.DialogRegister;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.databinding.LayoutActivityLoginViewBinding;

public class LoginActivity extends AppCompatAct<LayoutActivityLoginViewBinding> implements View.OnClickListener {
    private AppDatabase appDatabase;
    private DialogRegister dialogRegister;

    @Override
    protected void initLayout() {
        this.appDatabase = AppDatabase.getInstance(this);
        this.userModel = new UserModel();
        this.bd.setUser(userModel);
        UserModel userModelData = appDatabase.getStudentDao().checkExistsUser("admin");
        if (userModelData == null){
            userModelData = new UserModel();
            userModelData.setName("admin");
            userModelData.setPassword("admin");
            userModelData.setPermission(0);
            appDatabase.getStudentDao().insertUser(userModelData);
        }
        bd.btRegister.setOnClickListener(this);
        bd.btRequestLogin.setOnClickListener(this);
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
                dialogRegister = new DialogRegister(this, userModel -> {
                    UserModel userModelData = appDatabase.getStudentDao().checkExistsUser(userModel.getName());
                    if (userModelData != null) {
                        Toast.makeText(LoginActivity.this, "Tên đăng nhập đã tồn tại.", Toast.LENGTH_SHORT).show();
                    } else {
                        appDatabase.getStudentDao().insertUser(userModel);
                        dialogRegister.dismiss();
                    }
                });
                dialogRegister.show(true);
                break;
            case R.id.btRequestLogin:
                this.userModel = bd.getUser();
                UserModel userModelData = appDatabase.getStudentDao().login(userModel.getName(), userModel.getPassword());
                if (userModelData != null) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("idUser", userModelData.getIdUser());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Tài khoản chưa được đăng kí.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

}
