package com.example.retrofitrxjava.fragment.managerOrderFragment;

import android.os.Bundle;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.adapter.ViewPagerAdapter;
import com.example.retrofitrxjava.databinding.FragmentManageOrderBinding;
import com.example.retrofitrxjava.fragment.BaseFragment;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class ManageOrderFragment extends BaseFragment<FragmentManageOrderBinding> {
    public ArrayList<String> animalsArray = new ArrayList<>();

    public static ManageOrderFragment newInstance() {
        Bundle args = new Bundle();
        ManageOrderFragment fragment = new ManageOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void initLiveData() {

    }

    @Override
    protected void initFragment() {
        animalsArray.add("To Pay");
        animalsArray.add("To Receive");
        animalsArray.add("Completed");
        animalsArray.add("Cancel");

        binding.viewPagerOrder.setAdapter(new ViewPagerAdapter(this));

        // attaching tab mediator
        new TabLayoutMediator(binding.tabLayoutControl, binding.viewPagerOrder,
                (tab, position) -> tab.setText(animalsArray.get(position))).attach();
    }

    @Override
    protected int getID() {
        return R.layout.fragment_manage_order;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) activity).setTitle(getString(R.string.myPurchaseLabel));
    }
}
