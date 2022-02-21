package com.example.retrofitrxjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofitrxjava.BR;

import java.util.ArrayList;

public class MutilAdt<T> extends RecyclerView.Adapter<MutilAdt.ViewHolder> {
    private @LayoutRes
    int resId;
    private LayoutInflater inflater;
    private ArrayList<T> data;
    private ListItemListener listener;

    public MutilAdt(Context context, @LayoutRes int resId) {
        this.resId = resId;
        inflater = LayoutInflater.from(context);
    }
    public void setDt(ArrayList<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setListener(ListItemListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, resId, viewGroup, false);
        return new ViewHolder(binding);
    }
    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MutilAdt.ViewHolder viewHolder, int i) {
        T t = data.get(i);
        viewHolder.binding.setVariable(BR.item, t);
        viewHolder.binding.setVariable(BR.listener, listener);
        viewHolder.binding.executePendingBindings();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        public ViewHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public interface ListItemListener {
    }
}
