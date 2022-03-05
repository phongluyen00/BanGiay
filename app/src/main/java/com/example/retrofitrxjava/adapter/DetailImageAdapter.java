package com.example.retrofitrxjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.model.EBook;

import java.util.List;

public class DetailImageAdapter extends PagerAdapter {
    private List<EBook> data;
    private LayoutInflater inflater;

    public DetailImageAdapter(Context context, List<EBook> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
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
        View v = inflater.inflate(R.layout.item_product, container, false);
        EBook eBook = data.get(position);
        ImageView productImage = v.findViewById(R.id.img_banner);

        Glide.with(container.getContext())
                .load(eBook.getImage())
                .into(productImage);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public float getPageWidth (int position) {
        return 0.93f;
    }

}
