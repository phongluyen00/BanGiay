package com.example.retrofitrxjava.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.DetailEBookActivity;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.LayoutRecruitmentBinding;
import com.example.retrofitrxjava.model.EBook;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment<LayoutRecruitmentBinding> implements ItemOnclickListener<EBook>, MutilAdt.ListItemListener {

    private MutilAdt<EBook> eBookAdapter;
    private MutilAdt<EBook> authorAdapter;
    private MutilAdt<EBook> continueReadingAdapter;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
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
        showDialog();
        getAllData(products -> {
            eBookAdapter = new MutilAdt<>(activity, R.layout.item_trending_books);
            binding.rvRecruitment.setAdapter(eBookAdapter);
            eBookAdapter.setDt((ArrayList<EBook>) products);
            eBookAdapter.setListener(this);
        });

        // read

        db.collection("continue_reading").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<EBook> continueReadingList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    EBook product = documentSnapshot.toObject(EBook.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        continueReadingList.add(product);
                    }
                }
                continueReadingAdapter = new MutilAdt<>(activity,R.layout.item_continue_reading);
                binding.rcContinue.setAdapter(continueReadingAdapter);
                continueReadingAdapter.setDt(continueReadingList);
            }
        });

        // read author
        db.collection("db_author").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<EBook> authorList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    EBook product = documentSnapshot.toObject(EBook.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        authorList.add(product);
                    }
                }
                authorAdapter = new MutilAdt<>(activity,R.layout.item_account);
                binding.rclAuthor.setAdapter(authorAdapter);
                authorAdapter.setDt(authorList);
                dismissDialog();
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.layout_recruitment;
    }

    @Override
    public void onItemMediaClick(EBook eBook) {
        Intent intent = new Intent(activity, DetailEBookActivity.class);
        intent.putExtra("ebook", eBook);
        startActivity(intent);
    }
}
