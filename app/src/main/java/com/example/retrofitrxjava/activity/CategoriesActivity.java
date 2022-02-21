package com.example.retrofitrxjava.activity;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.ActivityCategoriesBinding;
import com.example.retrofitrxjava.model.Categories;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatAct<ActivityCategoriesBinding> {

    private MutilAdt<Categories> categoriesAdt;
    private List<Categories> categoriesList = new ArrayList<>();
    private String status = "";

    @Override
    protected void initLayout() {
        setTitle("Categories");
        if (getIntent() != null) {
            status = getIntent().getStringExtra("status");
        }
        db.collection("categories").whereEqualTo("status", status).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Categories categories = documentSnapshot.toObject(Categories.class);
                    if (categories != null) {
                        categories.setDocumentId(documentSnapshot.getId());
                        categoriesList.add(categories);
                    }
                }
                categoriesAdt = new MutilAdt<>(this, R.layout.item_categories);
                bd.rclCategories.setAdapter(categoriesAdt);
                categoriesAdt.setDt((ArrayList<Categories>) categoriesList);
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.activity_categories;
    }
}
