package com.example.retrofitrxjava.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.AppCompatAct;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.model.EBook;
import com.example.retrofitrxjava.model.Markets;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luyenphong on 13/11/2020.
 */
public abstract class BaseFragment<BD extends ViewDataBinding> extends Fragment {

    protected BD binding;
    protected Activity activity;
    protected UserModel userModel;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected FirebaseAuth mAuth;
    protected AppDatabase appDatabase;
    protected FirebaseUser currentUser;
    protected ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,getID(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appDatabase = AppDatabase.getInstance(activity);
        mAuth = FirebaseAuth.getInstance();
        currentUser =  mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        initFragment();
        initLiveData();
        initAdapter();
    }

    protected void getAllData(onLoadData loadData) {
        List<EBook> productList = new ArrayList<>();
        db.collection("db_comics").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    EBook product = documentSnapshot.toObject(EBook.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productList.add(product);
                    }
                }
                loadData.onDone(productList);
            }
        });
    }

    protected void getTrendingData(onLoadData loadData) {
        List<EBook> productList = new ArrayList<>();
        db.collection("db_comics").whereEqualTo("type","top").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    EBook product = documentSnapshot.toObject(EBook.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productList.add(product);
                    }
                }
                loadData.onDone(productList);
            }
        });
    }


    public interface onLoadData {
        void onDone(List<EBook> products);
    }

    protected void showDialog() {
        progressDialog.show();
    }

    protected void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public List<EBook> getListSearchUser(String textQuery, List<EBook> lst) {
        List<EBook> lstSearch = new ArrayList<>();
        for (EBook model : lst) {
            if (model.getTitle().toLowerCase().contains(textQuery.toLowerCase())) {
                lstSearch.add(model);
            }
        }
        return lstSearch;
    }

    protected String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    protected abstract void initAdapter();

    protected abstract void initLiveData();

    protected abstract void initFragment();

    protected abstract int getID();
}
