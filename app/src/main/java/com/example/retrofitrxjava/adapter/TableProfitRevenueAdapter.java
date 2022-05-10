package com.example.retrofitrxjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofitrxjava.databinding.ItemTableBinding;
import com.example.retrofitrxjava.model.ProfitModel;

import org.jsoup.helper.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TableProfitRevenueAdapter extends RecyclerView.Adapter<TableProfitRevenueAdapter.ViewHolder> {
    private List<ProfitModel> listData;
    private Context context;

    public TableProfitRevenueAdapter(Context context) {
        listData = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<ProfitModel> listData) {
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
        ItemTableBinding itemPhotoCategory = ItemTableBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemPhotoCategory);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfitModel item = listData.get(position);
        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemTableBinding itemCartBinding;

        ViewHolder(ItemTableBinding itemCartBinding) {
            super(itemCartBinding.getRoot());
            this.itemCartBinding = itemCartBinding;
        }

        void bindView(ProfitModel item) {
            itemCartBinding.tvProfit.setText(StringUtil.isBlank(item.getProfit()) ? "" : formatPrice(Double.parseDouble(item.getProfit())));
            itemCartBinding.tvRevenue.setText(StringUtil.isBlank(item.getRevenue()) ? "" : formatPrice(Double.parseDouble(item.getRevenue())));
            itemCartBinding.tvMarketProduct.setText(item.getNameProduct());
        }
    }

    protected static String formatPrice(double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String formattedNumber = formatter.format(price);
        return formattedNumber + "";
    }

}
