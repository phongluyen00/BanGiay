package com.example.retrofitrxjava.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.ActivityDetailTruyenBinding;

import java.util.List;

public class DetailActivity extends AppCompatAct<ActivityDetailTruyenBinding>{

    private Product article;

    @Override
    protected void initLayout() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("idUser", 0);
        userModel = this.appDatabase.getStudentDao().getUserModelId(id);
        article = (Product) getIntent().getSerializableExtra(HomeActivity.EXTRA_DATA);
        bd.setItem(article);
    }

    @Override
    protected int getID() {
        return R.layout.activity_detail_truyen;
    }

    public void add(View view){
        List<Product> all = appDatabase.getStudentDao().getAll(userModel.getIdUser());
        if (all != null) {
            for (Product article1 : appDatabase.getStudentDao().getAll(userModel.getIdUser())) {
                if (TextUtils.equals(article1.getName(), article.getName())) {
                    article1.setCount(article1.getCount() + 1);
                    appDatabase.getStudentDao().update(article1);
                    Toast.makeText(this, "Thêm giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        article.setCount(1);
        article.setIdUserModel(userModel.getIdUser());
        appDatabase.getStudentDao().insertProduct(article);
        Toast.makeText(this, "Thêm giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
    }

    public void push(View view){
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra("idUser", userModel.getIdUser());
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bd.img.setVisibility(View.VISIBLE);
        bd.ll.setVisibility(View.VISIBLE);
    }
}