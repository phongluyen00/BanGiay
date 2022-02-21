package com.example.retrofitrxjava.activity;

import android.view.View;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.PaymentSuccessBinding;

public class PaymentSuccessActivity extends AppCompatAct<PaymentSuccessBinding>{
    @Override
    protected void initLayout() {
        bd.openHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.payment_success;
    }
}
