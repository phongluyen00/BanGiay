package com.example.retrofitrxjava.dialog;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.databinding.BottomSheetBuyBinding;

public class BuyBottomSheet extends BaseBottomSheet<BottomSheetBuyBinding> {

    private itemListener listener;
    private UserModel userModel;
    private double totalPrice;

    public BuyBottomSheet(itemListener listener, double totalPrice) {
        this.listener = listener;
        this.userModel = MainActivity.userModel;
        this.totalPrice = totalPrice;
    }

    @Override
    protected int layoutId() {
        return R.layout.bottom_sheet_buy;
    }

    @Override
    protected void initLayout() {
        binding.setTotal(String.valueOf(totalPrice));
        binding.setUser(userModel);
        binding.submit.setOnClickListener(v -> {
            listener.onSubmit(1000);
            dismiss();
        });
    }

    public interface itemListener {
        void onSubmit(double totalPrice);
    }
}
