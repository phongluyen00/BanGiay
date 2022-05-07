package com.example.retrofitrxjava.activity;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.TableProfitRevenueAdapter;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.ActivityProfitRevenueReportBinding;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.model.ProfitModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfitRevenueReportActivity extends AppCompatAct<ActivityProfitRevenueReportBinding> {
    private TableProfitRevenueAdapter adapter;
    private ArrayList<ProfitModel> listProfitModel;

    @Override
    protected void initLayout() {
        adapter = new TableProfitRevenueAdapter(this);
        bd.recyclerItem.setAdapter(adapter);
        listProfitModel = new ArrayList<>();
        getMarket();
    }

    @Override
    protected int getID() {
        return R.layout.activity_profit_revenue_report;
    }

    public void getMarket() {
        db.collection("my_markets").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Markets product = documentSnapshot.toObject(Markets.class);
                    if (product != null) {
                        product.setDocumentId(documentSnapshot.getId());
                        ProfitModel profitModel = new ProfitModel();
                        profitModel.setNameProduct(product.getTitle());
                        profitModel.setMarketId(documentSnapshot.getId());
                        listProfitModel.add(profitModel);
                    }
                }
                getAllCart(listProfitModel);
            }
        });
    }

    private void getAllCart(ArrayList<ProfitModel> listProfitModel) {
        db.collection(Constants.KEY_CART)
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_COMPLETED)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, String> mapTotal = new HashMap<>();

                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories productCategories = documentSnapshot.toObject(ProductCategories.class);
                    if (productCategories != null) {
                        getProfit(productCategories, mapTotal);
                    }
                }
                for (int i = 0; i < listProfitModel.size(); i++) {
                    if(mapTotal.containsKey(listProfitModel.get(i).getMarketId())){
                        listProfitModel.get(i).setProfit(mapTotal.get(listProfitModel.get(i).getMarketId()));
                    }
                }

                adapter.setList(listProfitModel);
            }
        });
    }

    private void getProfit(ProductCategories productCategory, Map<String, String> mapTotal) {
        if (mapTotal.containsKey(productCategory.getId_markets())) {
            double price = getPrice(mapTotal.get(productCategory.getId_markets())) * productCategory.getCount() +
                    getPrice(productCategory.getPrice()) * productCategory.getCount();
            mapTotal.put(productCategory.getId_markets(), String.valueOf(price));
        } else {
            double price = getPrice(productCategory.getPrice()) * productCategory.getCount();
            mapTotal.put(productCategory.getId_markets(), String.valueOf(price));
        }
    }

    public double getPrice(String price) {
        return Double.parseDouble(price);
    }
}
