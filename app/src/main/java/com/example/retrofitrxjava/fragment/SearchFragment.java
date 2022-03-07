package com.example.retrofitrxjava.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.DetailEBookDialog;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.FragmentFavoriteBinding;
import com.example.retrofitrxjava.databinding.FragmentSearchBinding;
import com.example.retrofitrxjava.model.EBook;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment<FragmentSearchBinding> implements ItemOnclickListener<EBook>,MutilAdt.ListItemListener {

    private List<EBook> eBookArrayList = new ArrayList<>();
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
            eBookAdapter.setDt((ArrayList<EBook>) products);
            eBookAdapter.setListener(this);
        });

        binding.editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
        DetailEBookDialog detailEBookDialog = new DetailEBookDialog(eBook, position, eBookArrayList);
        detailEBookDialog.show(getChildFragmentManager(), detailEBookDialog.getTag());
    }

    @Override
    public void onRemove(EBook eBook, int position) {

    }
}
