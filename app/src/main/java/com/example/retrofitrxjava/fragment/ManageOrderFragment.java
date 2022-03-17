package com.example.retrofitrxjava.fragment;

import static com.example.retrofitrxjava.fragment.HomeFragment.EXTRA_DATA;

import android.content.Intent;
import android.os.Bundle;

import com.example.retrofitrxjava.ItemFavoriteListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.DetailActivity;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.FragmentFavoriteBinding;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageOrderFragment extends BaseFragment<FragmentFavoriteBinding> implements MutilAdt.ListItemListener, ItemFavoriteListener<ProductCategories> {

    private List<ProductCategories> productCategoriesList = new ArrayList<>();
    private MutilAdt<ProductCategories> favoriteAdapter;

    public static ManageOrderFragment newInstance() {
        Bundle args = new Bundle();
        ManageOrderFragment fragment = new ManageOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initLiveData() {

    }

    @Override
    protected void initFragment() {
        db.collection("shopping_favorite").whereEqualTo("uid", currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories productCategories = documentSnapshot.toObject(ProductCategories.class);
                    if (productCategories != null) {
                        productCategories.setDocumentId(documentSnapshot.getId());
                        productCategoriesList.add(productCategories);
                    }
                }

                favoriteAdapter = new MutilAdt<>(activity, R.layout.item_favorite);
                binding.rclFavorite.setAdapter(favoriteAdapter);
                favoriteAdapter.setDt((ArrayList<ProductCategories>) productCategoriesList);
                favoriteAdapter.setListener(this);
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.fragment_favorite;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) activity).setTitle("Favorite");
    }

    @Override
    public void onClickProduct(ProductCategories productCategories) {
        productCategories.setFavorite(true);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(EXTRA_DATA, productCategories);
        startActivity(intent);
    }

    @Override
    public void onClickRemove(ProductCategories productCategories) {
        showDialog();
        db.collection("shopping_favorite").document(productCategories.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Snackbar snackbar = Snackbar
                            .make(binding.mainContent, "Delete success", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    productCategoriesList.remove(productCategories);
                    favoriteAdapter.setDt((ArrayList<ProductCategories>) productCategoriesList);
                    dismissDialog();
                })
                .addOnFailureListener(e -> {
                    dismissDialog();
                    Snackbar snackbar = Snackbar
                            .make(binding.mainContent, "Delete failed", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    dismissDialog();
                });
    }
}
