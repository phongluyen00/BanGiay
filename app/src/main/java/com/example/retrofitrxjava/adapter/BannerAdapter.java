package com.example.retrofitrxjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.example.retrofitrxjava.BR;
import com.example.retrofitrxjava.databinding.ItemBannerBinding;

import java.util.ArrayList;

public class BannerAdapter<T> extends PagerAdapter {

    private ArrayList<T> data;
    private @LayoutRes
    int resId;
    LayoutInflater mLayoutInflater;

    public BannerAdapter(Context context, ArrayList<T> data, int resId) {
        this.data = data;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resId = resId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ItemBannerBinding binding = DataBindingUtil.inflate(mLayoutInflater,resId, container, false);
        T t = data.get(position);
        binding.setVariable(BR.item, t);
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
