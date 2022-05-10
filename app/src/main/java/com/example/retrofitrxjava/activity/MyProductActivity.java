package com.example.retrofitrxjava.activity;

import android.view.View;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.ActivityMyProductBinding;
import com.example.retrofitrxjava.model.ProductCategories;

public class MyProductActivity extends AppCompatAct<ActivityMyProductBinding>{

    @Override
    protected void initLayout() {
        bd.back.setOnClickListener(view -> finish());
        MutilAdt<ProductCategories> adapter = new MutilAdt<>(this, R.layout.item_my_product_bd);
        adapter.setDt(MainActivityAdmin.productCategoriesList);
        bd.rcl.setAdapter(adapter);
    }

    @Override
    protected int getID() {
        return R.layout.activity_my_product;
    }
}
