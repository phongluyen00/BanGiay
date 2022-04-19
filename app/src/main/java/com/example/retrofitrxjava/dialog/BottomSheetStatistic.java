package com.example.retrofitrxjava.dialog;

import android.view.View;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.ActivityStatisticBinding;

public class BottomSheetStatistic extends BaseBottomSheet<ActivityStatisticBinding>{
    @Override
    protected int layoutId() {
        return R.layout.activity_statistic;
    }

    @Override
    protected void initLayout() {
        binding.viewKqkd.setOnClickListener(view -> {
            BottomSheetKetQuaKinhDoanh ketQuaKinhDoanh = new BottomSheetKetQuaKinhDoanh();
            ketQuaKinhDoanh.show(getChildFragmentManager(),ketQuaKinhDoanh.getTag());
        });
    }
}
