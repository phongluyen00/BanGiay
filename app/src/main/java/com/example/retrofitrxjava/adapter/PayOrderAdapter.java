package com.example.retrofitrxjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofitrxjava.BR;
import com.example.retrofitrxjava.databinding.ItemOrderPaymentBinding;
import com.example.retrofitrxjava.model.ProductCategories;

import java.util.ArrayList;
import java.util.List;

public class PayOrderAdapter extends RecyclerView.Adapter<PayOrderAdapter.ViewHolder> {
    private List<ProductCategories> listData;
    private Context context;

    public PayOrderAdapter(Context context) {
        listData = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<ProductCategories> listData) {
        if (this.listData == null) {
            this.listData = new ArrayList<>();
        }
        if (listData == null) {
            return;
        }
        this.listData = listData;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderPaymentBinding itemPhotoCategory = ItemOrderPaymentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemPhotoCategory);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCategories item = listData.get(position);
        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemOrderPaymentBinding itemCartBinding;

        ViewHolder(ItemOrderPaymentBinding itemCartBinding) {
            super(itemCartBinding.getRoot());
            this.itemCartBinding = itemCartBinding;
        }

        void bindView(ProductCategories item) {
            itemCartBinding.setVariable(BR.item, item);
            itemCartBinding.tvTitle.setText(item.getTitle());
            itemCartBinding.tvPrice.setText(item.getPrice());
            itemCartBinding.tvCount.setText(String.valueOf(item.getCount()));
        }
    }
}
