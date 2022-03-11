package com.example.retrofitrxjava.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.DetailEBookDialog;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.FragmentFavoriteBinding;
import com.example.retrofitrxjava.model.EBook;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseFragment<FragmentFavoriteBinding> implements ItemOnclickListener<EBook>,MutilAdt.ListItemListener {

    private MutilAdt<EBook> favoriteAdapter;
    private List<EBook> eBookList = new ArrayList<>();

    public static FavoriteFragment newInstance() {

        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
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
        db.collection("book_favorite").whereEqualTo("uid", currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    EBook eBook = documentSnapshot.toObject(EBook.class);
                    if (eBook != null) {
                        eBook.setDocumentId(documentSnapshot.getId());
                        eBookList.add(eBook);
                    }
                }

                favoriteAdapter = new MutilAdt<>(activity, R.layout.item_favorite);
                binding.rclFavorite.setAdapter(favoriteAdapter);
                favoriteAdapter.setDt((ArrayList<EBook>) eBookList);
                favoriteAdapter.setListener(this);
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.fragment_favorite;
    }

    @Override
    public void onItemBookClick(EBook eBook, int position) {
        Intent intent = new Intent(activity, DetailEBookDialog.class);
        intent.putExtra("ebook", eBook);
        intent.putExtra("index", position);
        intent.putExtra("list", (Serializable) eBookList);
        startActivity(intent);
    }

    @Override
    public void onRemove(EBook eBook, int position) {
        showDialog();
        db.collection("book_favorite").document(eBook.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Snackbar snackbar = Snackbar
                            .make(binding.main, "Delete success", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    eBookList.remove(eBook);
                    favoriteAdapter.setDt((ArrayList<EBook>) eBookList);
                    dismissDialog();
                })
                .addOnFailureListener(e -> {
                    dismissDialog();
                    Snackbar snackbar = Snackbar
                            .make(binding.main, "Delete failed", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    dismissDialog();
                });
    }
}
