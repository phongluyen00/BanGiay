package com.example.retrofitrxjava.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.database.AppDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class AppCompatAct<BD extends ViewDataBinding> extends AppCompatActivity {
    protected BD bd;
    protected Executor executor;
    protected BiometricPrompt biometricPrompt;
    protected BiometricPrompt.PromptInfo promptInfo;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected AppDatabase appDatabase;
    protected UserModel userModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = DataBindingUtil.setContentView(this, getID());
        executor = ContextCompat.getMainExecutor(this);
        appDatabase = AppDatabase.getInstance(this);
        initLayout();
    }

    protected void getAllData(onLoadData loadData) {
        List<Product> productList = new ArrayList<>();
        db.collection("product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        productList.add(product);
                    }
                }
                loadData.onDone(productList);
            }
        });
    }

    protected static String formatNumber(double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String formattedNumber = formatter.format(price);
        return formattedNumber + " vnd";
    }

    @BindingAdapter("thumb")
    public static void setThumb(ImageView im, String img) {
        Glide.with(im)
                .load(img)
                .error(R.drawable.ic_baseline_warning_24)
                .into(im);
    }

    @BindingAdapter("price")
    public static void setPrice(AppCompatTextView tv, String price) {
        tv.setText(formatNumber(Double.parseDouble(price)));
    }

    public interface onLoadData {
        void onDone(List<Product> products);
    }

    protected abstract void initLayout();

    protected abstract int getID();
}
