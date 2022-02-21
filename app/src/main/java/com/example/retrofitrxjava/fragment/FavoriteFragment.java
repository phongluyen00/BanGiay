package com.example.retrofitrxjava.fragment;

import android.os.Bundle;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.FragmentFavoriteBinding;

public class FavoriteFragment extends BaseFragment<FragmentFavoriteBinding> {

    public static FavoriteFragment newInstance() {

        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
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

    }

    @Override
    protected int getID() {
        return R.layout.fragment_favorite;
    }
}
