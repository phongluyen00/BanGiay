package com.example.retrofitrxjava.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.example.retrofitrxjava.UserModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class BottomSheetFragment<BD extends ViewDataBinding> extends BottomSheetDialogFragment {

    protected BD binding;
    protected DatabaseReference databaseReference;
    protected Activity activity;
    protected UserModel userModel;
    protected FirebaseUser firebaseUser;
    protected FirebaseAuth mAuth;

    public BottomSheetFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                getLayoutId(), null, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        activity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =  mAuth.getCurrentUser();
        initLayout();
        return binding.getRoot();
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected abstract void initLayout();

    protected abstract int getLayoutId();
}
