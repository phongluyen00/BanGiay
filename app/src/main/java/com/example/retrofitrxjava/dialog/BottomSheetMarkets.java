package com.example.retrofitrxjava.dialog;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.ItemOnclickListener;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.BottomSheetMarketsBinding;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetMarkets extends BaseBottomSheet<BottomSheetMarketsBinding> implements MutilAdt.ListItemListener, ItemOnclickListener<Markets> {

    private listener listener;
    private MutilAdt<Markets> marketsMutilAdt;
    private List<Markets> list;

    public BottomSheetMarkets(BottomSheetMarkets.listener listener, List<Markets> list) {
        this.listener = listener;
        this.list = list;
    }

    @Override
    protected int layoutId() {
        return R.layout.bottom_sheet_markets;
    }

    @Override
    protected void initLayout() {
        marketsMutilAdt = new MutilAdt<>(getActivity(), R.layout.item_markst);
        binding.rcl.setAdapter(marketsMutilAdt);
        if (list != null && list.size() > 0){
            marketsMutilAdt.setDt((ArrayList<Markets>) list);
        }
        marketsMutilAdt.setListener(this);
    }

    @Override
    public void onItemMediaClick(Markets markets) {
        listener.onClick(markets);
        dismiss();
    }

    public interface listener {
        void onClick(Markets markets);
    }
}
