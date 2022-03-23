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
    private static final int CARD_ITEM_SIZE = 3;
    public ViewPagerAdapter(@NonNull ManageOrderFragment fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull @Override public Fragment createFragment(int position) {
        switch (position)
        {
            case 0: return new PayOrderFragment();
            case 1: return new ReceiveOrderFragment();
            case 2: return new CompletedOrderFragment();
            default: return new CancelOrderFragment();
        }
    }

    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}