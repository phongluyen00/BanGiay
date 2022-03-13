package com.example.retrofitrxjava.dialog;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.databinding.BottomSheetBuyBinding;
import com.razorpay.Checkout;

import org.json.JSONObject;

public class BuyBottomSheet extends BaseBottomSheet<BottomSheetBuyBinding> {

    private itemListener listener;
    private UserModel userModel;
    private double totalPrice;

    public BuyBottomSheet(itemListener listener, UserModel userModel, double totalPrice) {
        this.listener = listener;
        this.userModel = userModel;
        this.totalPrice = totalPrice;
    }

    @Override
    protected int layoutId() {
        return R.layout.bottom_sheet_buy;
    }

    @Override
    protected void initLayout() {
        binding.submit.setOnClickListener(v -> {
            listener.onSubmit();
            dismiss();
        });
    }

    public interface itemListener{
        void onSubmit();
    }
}
