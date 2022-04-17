package com.example.retrofitrxjava.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBottomSheet<BD extends ViewDataBinding> extends BottomSheetDialogFragment {

    protected BD binding;
    protected FirebaseUser firebaseUser;
    protected FirebaseAuth firebaseAuth;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =  firebaseAuth.getCurrentUser();
        initLayout();
        return binding.getRoot();
    }

    public List<ProductCategories> getListSearch(String textQuery, List<ProductCategories> lst) {
        List<ProductCategories> lstSearch = new ArrayList<>();
        for (ProductCategories model : lst) {
            if (model.getTitle().toLowerCase().contains(textQuery.toLowerCase())) {
                lstSearch.add(model);
            }
        }
        return lstSearch;
    }

    protected abstract int layoutId();

    protected abstract void initLayout();
}
