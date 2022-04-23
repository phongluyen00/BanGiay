package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.PaymentSuccessBinding;

public class PaymentSuccessActivity extends AppCompatAct<PaymentSuccessBinding>{

    public static long total;

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        bd.tvTotal.setText(total + "$");
        bd.openHome.setOnClickListener(v -> finish());
    }

    @Override
    protected int getID() {
        return R.layout.payment_success;
    }
}
