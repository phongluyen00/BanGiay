package com.example.retrofitrxjava.dialog;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.BottomSheetBuyBinding;
import com.razorpay.Checkout;

import org.json.JSONObject;

public class BuyBottomSheet extends BaseBottomSheet<BottomSheetBuyBinding> {

    @Override
    protected int layoutId() {
        return R.layout.bottom_sheet_buy;
    }

    @Override
    protected void initLayout() {
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });
    }

    public void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_bvPYonKyVPrUPM");
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Merchant Name");
            options.put("description", "Payment");
            options.put("currency", "INR");
            options.put("amount", "300");//pass amount in currency subunits
            options.put("prefill.email", firebaseUser.getEmail());
            options.put("prefill.contact",firebaseUser.getPhoneNumber());

            checkout.open(getActivity(), options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }
}
