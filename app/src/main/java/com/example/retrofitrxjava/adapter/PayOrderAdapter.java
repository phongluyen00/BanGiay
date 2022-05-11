package com.example.retrofitrxjava.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofitrxjava.BR;
import com.example.retrofitrxjava.activity.AppCompatAct;
import com.example.retrofitrxjava.databinding.ItemOrderPaymentBinding;
import com.example.retrofitrxjava.databinding.ItemOrderPaymentTestBinding;
import com.example.retrofitrxjava.model.Bill;
import com.example.retrofitrxjava.model.ProductCategories;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PayOrderAdapter extends RecyclerView.Adapter<PayOrderAdapter.ViewHolder> {
    private List<Bill> listData;
    private Context context;
    private Boolean isShowCancel = false;
    private ListItemListener callback;
    private boolean isTypeAdmin;

    public void setTypeAdmin(boolean typeAdmin) {
        isTypeAdmin = typeAdmin;
    }

    public PayOrderAdapter(Context context) {
        listData = new ArrayList<>();
        this.context = context;
    }

    public void setCallback(ListItemListener callback) {
        this.callback = callback;
    }

    public void setShowCancel(Boolean showCancel) {
        isShowCancel = showCancel;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Bill> listData) {
        if (this.listData == null) {
            this.listData = new ArrayList<>();
        }
        if (listData == null) {
            return;
        }
        this.listData = listData;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        listData.remove(position);
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
        Bill item = listData.get(position);
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

        void bindView(Bill item) {
            if (item.getProductCategoriesList() != null && item.getProductCategoriesList().size() > 0 && item.getProductCategoriesList().get(0) != null) {
                itemCartBinding.setVariable(BR.item, item.getProductCategoriesList().get(0));
                for (int i = 1; i < item.getProductCategoriesList().size(); i++) {
                    ItemOrderPaymentTestBinding layout1 = ItemOrderPaymentTestBinding.inflate(LayoutInflater.from(context), itemCartBinding.viewItemOrderChild, false);
                    layout1.setVariable(BR.item, item.getProductCategoriesList().get(i));

                    itemCartBinding.viewItemOrderChild.addView(layout1.getRoot());
                }
            }
            itemCartBinding.tvAccept.setVisibility(isTypeAdmin ? View.VISIBLE : View.GONE);
            itemCartBinding.tvCancelOrder.setVisibility(isShowCancel ? View.VISIBLE : View.GONE);
            itemCartBinding.tvTotalOrder.setText(formatNumber(setPriceTotal(item)));
            itemCartBinding.tvAccept.setOnClickListener(v -> callback.clickItemAcceptOrder(item, getAdapterPosition()));
            itemCartBinding.tvCancelOrder.setOnClickListener(v -> callback.clickCancelOrder(item, getAdapterPosition()));
        }
    }

    public double setPriceTotal(Bill item) {
        double price = 0;
        if (item.getProductCategoriesList() != null) {
            for (ProductCategories article : item.getProductCategoriesList()) {
                price += getPrice(article.getPrice()) * article.getCount();
            }
        }
        return price;
    }

    protected static String formatNumber(double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String formattedNumber = formatter.format(price);
        return formattedNumber + "";
    }

    public double getPrice(String price) {
        return Double.parseDouble(price);
    }

    public interface ListItemListener {
        void clickCancelOrder(Bill item, int position);

        void clickItemAcceptOrder(Bill item, int position);
    }
}
