package com.example.retrofitrxjava.fragment.managerOrderFragment;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.MainActivity;
import com.example.retrofitrxjava.activity.MainActivityAdmin;
import com.example.retrofitrxjava.adapter.ViewPagerAdapter;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.FragmentManageOrderBinding;
import com.example.retrofitrxjava.fragment.BaseFragment;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class ManageOrderFragment extends BaseFragment<FragmentManageOrderBinding> {
    public ArrayList<String> animalsArray = new ArrayList<>();
    Boolean isTypeAdmin = false;

    public static ManageOrderFragment newInstance(Boolean isTypeAdmin) {
        Bundle args = new Bundle();
        args.putBoolean(Constants.ARG_TYPE_ADMIN, isTypeAdmin);
        ManageOrderFragment fragment = new ManageOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isTypeAdmin = getArguments().getBoolean(Constants.ARG_TYPE_ADMIN);
        if (!isTypeAdmin) {
            ((MainActivity) activity).setTitle(getString(R.string.myPurchaseLabel));
        } else {
            ((MainActivityAdmin) activity).setTitle(getString(R.string.myPurchaseLabel));
        }
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

        binding.viewPagerOrder.setAdapter(new ViewPagerAdapter(this, isTypeAdmin));

        // attaching tab mediator
        new TabLayoutMediator(binding.tabLayoutControl, binding.viewPagerOrder,
                (tab, position) -> tab.setText(animalsArray.get(position))).attach();
    }

    @Override
    protected int getID() {
        return R.layout.fragment_manage_order;
    }
}
