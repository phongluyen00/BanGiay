package com.example.retrofitrxjava.dialog;

import android.content.Intent;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.activity.BusinessReportActivity;
import com.example.retrofitrxjava.activity.ProfitRevenueReportActivity;
import com.example.retrofitrxjava.databinding.ActivityStatisticBinding;

public class BottomSheetStatistic extends BaseBottomSheet<ActivityStatisticBinding> {
    @Override
    protected int layoutId() {
        return R.layout.activity_statistic;
    }

    @Override
    protected void initLayout() {
        binding.viewKqkd.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), BusinessReportActivity.class));
        });
        binding.tvProfitRevenueReport.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ProfitRevenueReportActivity.class));
        });
    }
}
