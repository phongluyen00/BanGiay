package com.example.retrofitrxjava.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.database.AppDatabase;
import com.example.retrofitrxjava.model.Markets;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.helper.StringUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
    protected FirebaseAuth mAuth;
    protected FirebaseUser currentUser;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = DataBindingUtil.setContentView(this, getID());
        executor = ContextCompat.getMainExecutor(this);
        appDatabase = AppDatabase.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser =  mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        initLayout();
    }

    protected void getAllData(onLoadData loadData) {
        List<Markets> productList = new ArrayList<>();
        db.collection("product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Markets product = documentSnapshot.toObject(Markets.class);
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
//        DecimalFormat formatter = new DecimalFormat("###,###,###");
//        String formattedNumber = formatter.format(price);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        String pattern = "$#,##0.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        BigDecimal bigDecimal = new BigDecimal(price);

        String bigDecimalConvertedValue = decimalFormat.format(bigDecimal);
        return bigDecimalConvertedValue;
    }

    @BindingAdapter("thumb")
    public static void setThumb(ImageView im, String img) {
        Glide.with(im)
                .load(img)
                .error(R.drawable.ic_baseline_no_accounts_24)
                .into(im);
    }

    protected String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    @BindingAdapter("price")
    public static void setPrice(AppCompatTextView tv, String price) {
        if (StringUtil.isBlank(price)){
            tv.setText("");
            return;
        }
        tv.setText(formatNumber(Double.parseDouble(price)));
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

    public static Spanned textHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }
        else {
            return Html.fromHtml(html);
        }
    }

    @BindingAdapter("textHtml")
    public static void textHtml(AppCompatTextView tv, String text) {
        if (StringUtil.isBlank(text)){
            tv.setText("");
            return;
        }
        tv.setText(textHtml(text));
    }

    public interface onLoadData {
        void onDone(List<Markets> products);
    }

    protected abstract void initLayout();

    protected abstract int getID();
}
