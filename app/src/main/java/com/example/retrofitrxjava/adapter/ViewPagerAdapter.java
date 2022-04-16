package com.example.retrofitrxjava.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.retrofitrxjava.fragment.managerOrderFragment.CancelOrderFragment;
import com.example.retrofitrxjava.fragment.managerOrderFragment.CompletedOrderFragment;
import com.example.retrofitrxjava.fragment.managerOrderFragment.ManageOrderFragment;
import com.example.retrofitrxjava.fragment.managerOrderFragment.PayOrderFragment;
import com.example.retrofitrxjava.fragment.managerOrderFragment.ReceiveOrderFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 4;
    private Boolean isTypeAdmin;

    public ViewPagerAdapter(@NonNull ManageOrderFragment fragmentActivity, Boolean isTypeAdmin) {
        super(fragmentActivity);
        this.isTypeAdmin = isTypeAdmin;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PayOrderFragment.newInstance(isTypeAdmin);
            case 1:
                return ReceiveOrderFragment.newInstance(isTypeAdmin);
            case 2:
                return CompletedOrderFragment.newInstance(isTypeAdmin);
            default:
                return CancelOrderFragment.newInstance(isTypeAdmin);
        }
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}