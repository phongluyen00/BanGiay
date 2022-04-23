package com.example.retrofitrxjava.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.DetailEBookDialog;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.FragmentFavoriteBinding;
import com.example.retrofitrxjava.databinding.FragmentSearchBinding;
import com.example.retrofitrxjava.model.EBook;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment<FragmentSearchBinding> implements ItemOnclickListener<EBook>,MutilAdt.ListItemListener {

    private List<EBook> eBookArrayList = new ArrayList<>();
    private List<EBook> eBookListPush = new ArrayList<>();
    private MutilAdt<EBook> eBookAdapter;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
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
        getAllData(products -> {
            eBookArrayList.clear();
            eBookArrayList.addAll(products);
            eBookAdapter = new MutilAdt<>(activity, R.layout.item_search);
            binding.rclAll.setAdapter(eBookAdapter);
            eBookAdapter.setDt((ArrayList<EBook>) eBookArrayList);
            eBookAdapter.setListener(this);
        });

        binding.editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0){
                    eBookListPush = (ArrayList<EBook>) getListSearchUser(s.toString(), eBookArrayList);
                }else {
                    eBookListPush = eBookArrayList;
                }
                eBookAdapter.setDt((ArrayList<EBook>) getListSearchUser(s.toString(), eBookArrayList));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.fragment_search;
    }

    @Override
    public void onItemBookClick(EBook eBook, int position) {
        Intent intent = new Intent(activity, DetailEBookDialog.class);
        Log.d("AAAAAAAAAAA", eBook.getDocumentId() + "--------" + eBook.getId_book());
        intent.putExtra("ebook", eBook);
        intent.putExtra("index", position);
        intent.putExtra("list", (Serializable) eBookListPush);
        startActivity(intent);
    }

    @Override
    public void onRemove(EBook eBook, int position) {

    }
}
