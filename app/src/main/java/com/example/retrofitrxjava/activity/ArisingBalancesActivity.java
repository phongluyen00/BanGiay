package com.example.retrofitrxjava.activity;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.retrofitrxjava.ModelManager;
import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.ActivityArisingBalancesBinding;
import com.example.retrofitrxjava.viewmodel.SetupViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArisingBalancesActivity extends AppCompatAct<ActivityArisingBalancesBinding> {

    private SetupViewModel stSetupViewModel;
    private MutilAdt<ModelManager> adaMutilAdt;
    private List<ModelManager> list = new ArrayList<>();
    long totalHave = 0, totalDebt = 0;

    @Override
    protected void initLayout() {
        stSetupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);
        stSetupViewModel.loadManager(db, currentUser);
        initObserver();
        bd.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initObserver() {
        stSetupViewModel.getListModelManagerMutableLiveData().observe(this, new Observer<List<ModelManager>>() {
            @Override
            public void onChanged(List<ModelManager> modelManagers) {
                loadData(modelManagers);
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void loadData(List<ModelManager> productCategories) {
        list.clear();
        list.addAll(productCategories);
        adaMutilAdt = new MutilAdt<>(this, R.layout.item_manager);
        bd.rcl.setAdapter(adaMutilAdt);
        adaMutilAdt.setDt((ArrayList<ModelManager>) productCategories);

        for (ModelManager model : list) {
            if (!model.getIn_debt().equalsIgnoreCase("")){
                totalDebt += Long.parseLong(model.getIn_debt());
            }

            if (!model.getHave().equalsIgnoreCase("")){
                totalHave += Long.parseLong(model.getHave());
            }
        }

        bd.setTotalDebt(String.valueOf(totalDebt));
        bd.setTotalHave(String.valueOf(totalHave));
    }

    @Override
    protected int getID() {
        return R.layout.activity_arising_balances;
    }
}
