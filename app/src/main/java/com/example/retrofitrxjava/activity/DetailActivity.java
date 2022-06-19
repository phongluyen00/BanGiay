package com.example.retrofitrxjava.activity;


import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.ActivityDetailTruyenBinding;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;
import com.google.android.material.snackbar.Snackbar;

import static com.example.retrofitrxjava.fragment.HomeFragment.EXTRA_DATA;

public class DetailActivity extends AppCompatAct<ActivityDetailTruyenBinding> {

    private ProductCategories productCategories;
    private boolean isFavorite;
    private int count = 0;
    private SetupViewModel setupViewModel;

    @Override
    protected void initLayout() {
        Intent intent = getIntent();
        userModel = MainActivity.userModel;
        bd.setAccount(userModel);
        setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        if (intent != null) {
            productCategories = (ProductCategories) intent.getSerializableExtra(EXTRA_DATA);
            bd.setItem(productCategories);
        }


        setupViewModel.getProductCart().observe(this, new Observer<ProductCategories>() {
            @Override
            public void onChanged(ProductCategories product) {
                if (product == null) {
                    productCategories.setId_document(productCategories.getDocumentId());
                    productCategories.setUid(currentUser.getUid());
                    productCategories.setCount(count);
                    db.collection("cart").document().set(productCategories);
                } else {
                    product.setCount(product.getCount() + count);
                    db.collection("cart").document(product.getDocumentId()).set(product);
                }
                Toast.makeText(DetailActivity.this, "Thêm thành công !" + count, Toast.LENGTH_SHORT).show();

            }
        });

        if (!productCategories.isFavorite()) {
            setupViewModel.getDocumentIdFavorite(db, currentUser, productCategories);
        } else {
            isFavorite = true;
            bd.favorite.setImageResource(R.drawable.ic_baseline_favorite_24_new);
        }

        setupViewModel.getProductCategoriesMutableLiveData().observe(this, productCategories -> {
            if (productCategories != null) {
                isFavorite = true;
                bd.favorite.setImageResource(R.drawable.ic_baseline_favorite_24_new);
            }
        });

        bd.favorite.setVisibility(userModel.getPermission() == 1 ? View.GONE : View.VISIBLE);

        bd.favorite.setOnClickListener(v -> addFavorite(productCategories));

        bd.back.setOnClickListener(v -> finish());
        bd.icAdd.setOnClickListener(v -> {
            if (count == Long.parseLong(productCategories.getQuantity())) {
                return;
            }
            count++;
            bd.count.setText(String.valueOf(count));
        });

        bd.icDelete.setOnClickListener(v -> {
            if (count == 0) {
                return;
            }
            count--;
            bd.count.setText(String.valueOf(count));
        });

        bd.add.setOnClickListener(v -> {
            if (count != 0) {
                setupViewModel.checkProductExitsCart(db, currentUser, productCategories);
            } else {
                Snackbar snackbar = Snackbar
                        .make(bd.mainContent, "You need to enter the quantity", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

    }

    public void add(View view) {
//        List<Product> all = appDatabase.getStudentDao().getAll(userModel.getIdUser());
//        if (all != null) {
//            for (Product article1 : appDatabase.getStudentDao().getAll(userModel.getIdUser())) {
//                if (TextUtils.equals(article1.getName(), article.getName())) {
//                    article1.setCount(article1.getCount() + 1);
//                    appDatabase.getStudentDao().update(article1);
//                    Toast.makeText(this, "Thêm giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        }
//        article.setCount(1);
//        article.setIdUserModel(userModel.getIdUser());
//        appDatabase.getStudentDao().insertProduct(article);
//        Toast.makeText(this, "Thêm giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected int getID() {
        return R.layout.activity_detail_truyen;
    }

    private void addFavorite(ProductCategories productCategories) {
        if (!isFavorite) {
            productCategories.setId_document(productCategories.getDocumentId());
            productCategories.setUid(currentUser.getUid());
            bd.favorite.setImageResource(R.drawable.ic_baseline_favorite_24_new);
            db.collection("shopping_favorite").document().set(productCategories);
            isFavorite = true;
        } else {
            Snackbar snackbar = Snackbar
                    .make(bd.mainContent, "The product has been liked !", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }
}