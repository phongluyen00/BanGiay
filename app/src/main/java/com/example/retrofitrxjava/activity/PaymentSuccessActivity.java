package com.example.retrofitrxjava.activity;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.PaymentSuccessBinding;

public class PaymentSuccessActivity extends AppCompatAct<PaymentSuccessBinding>{
    @Override
    protected void initLayout() {
        bd.openHome.setOnClickListener(v -> finish());
    }

    @Override
    protected int getID() {
        return R.layout.payment_success;
    }
}
