package com.example.retrofitrxjava.activity;

import android.util.Log;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.ActivityCategoriesBinding;
import com.example.retrofitrxjava.model.Categories;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatAct<ActivityCategoriesBinding> {

    private MutilAdt<ProductCategories> categoriesAdt;
    private List<ProductCategories> productCategoriesList = new ArrayList<>();
    private Markets markets;
    private String id_Markets = "";

    @Override
    protected void initLayout() {
        setTitle("Categories");
        if (getIntent() != null) {
            markets = (Markets) getIntent().getSerializableExtra("markets");
            id_Markets = markets.getDocumentId();
        }
        db.collection("product_markets").whereEqualTo("id_markets", id_Markets).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories product = documentSnapshot.toObject(ProductCategories.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productCategoriesList.add(product);
                    }
                }
                categoriesAdt = new MutilAdt<>(this, R.layout.item_product);
//                categoriesAdt.setListener(this);
                bd.rclCategories.setAdapter(categoriesAdt);
                categoriesAdt.setDt((ArrayList<ProductCategories>) productCategoriesList);
            }
        });

    }

    @Override
    protected int getID() {
        return R.layout.activity_categories;
    }
}
