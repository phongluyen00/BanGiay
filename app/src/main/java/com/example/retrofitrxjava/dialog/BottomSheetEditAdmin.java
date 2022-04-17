package com.example.retrofitrxjava.dialog;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.databinding.BottomSheetEditBinding;


public class BottomSheetEditAdmin extends BaseBottomSheet<BottomSheetEditBinding> {

    @Override
    protected int layoutId() {
        return R.layout.bottom_sheet_edit;
    }

    @Override
    protected void initLayout() {
        binding.setItem(MainActivity.userModel);
        binding.save.setOnClickListener(view -> {
            userModel = binding.getItem();
            db.collection("account").document(firebaseUser.getUid()).set(userModel);
        });

    }

    public interface itemListener {
        void onSubmit(double totalPrice);
    }
}
