package com.example.retrofitrxjava.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.adapter.TableProfitRevenueAdapter;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.ActivityProfitRevenueReportBinding;
import com.example.retrofitrxjava.model.Markets;
import com.example.retrofitrxjava.model.ProductCategories;
import com.example.retrofitrxjava.model.ProfitModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfitRevenueReportActivity extends AppCompatAct<ActivityProfitRevenueReportBinding> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private TableProfitRevenueAdapter adapter;
    private ArrayList<ProfitModel> listProfitModel;

    @Override
    protected void initLayout() {
        initSpinner();
        adapter = new TableProfitRevenueAdapter(this);
        bd.recyclerItem.setAdapter(adapter);
        listProfitModel = new ArrayList<>();
        bd.back.setOnClickListener(v -> onBackPressed());
    }

    private void initSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Hôm nay");
        list.add("7 ngày trước");
        list.add("Tất cả");
        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list);
        bd.spinnerDate.setAdapter(spinnerAdapter);
        bd.spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String msg = "position :" + position + " value :" + list.get(position);
                Toast.makeText(ProfitRevenueReportActivity.this, msg, Toast.LENGTH_SHORT).show();
                listProfitModel.clear();
                getMarket(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(ProfitRevenueReportActivity.this, "onNothingSelected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.activity_profit_revenue_report;
    }

    public void getMarket(int position) {
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
                getAllCart(listProfitModel, position);
            }
        });
    }

    private void getAllCart(ArrayList<ProfitModel> listProfitModel, int position) {
        db.collection(Constants.KEY_CART)
                .whereEqualTo(Constants.KEY_STATUS, Constants.KEY_ITEM_COMPLETED)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, String> mapTotal = new HashMap<>();
                Map<String, String> mapTotalProfit = new HashMap<>();

                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories productCategories = documentSnapshot.toObject(ProductCategories.class);
                    for (int i = 0; i < listProfitModel.size(); i++) {
                        if (listProfitModel.get(i).getMarketId().equals(productCategories.getId_markets())) {
                            listProfitModel.get(i).setDate(productCategories.getTimeUpdate());
                        }
                    }
                    if (productCategories != null) {
                        getRevenue(productCategories, mapTotal);
                        getProfit(productCategories, mapTotalProfit);
                    }
                }

                for (int i = 0; i < listProfitModel.size(); i++) {
                    //lấy all
                    if (position == 2) {
                        if (mapTotal.containsKey(listProfitModel.get(i).getMarketId())) {
                            listProfitModel.get(i).setRevenue(mapTotal.get(listProfitModel.get(i).getMarketId()));
                            listProfitModel.get(i).setProfit(mapTotalProfit.get(listProfitModel.get(i).getMarketId()));
                        }
                    } else if (position == 1) {
                        //lấy 7 ngày
                        Date date = new Date();
                        date.setTime(listProfitModel.get(i).getDate());
                        String strDate = formatter.format(date);
                        if (getListDateAgo().contains(strDate)) {
                            if (mapTotal.containsKey(listProfitModel.get(i).getMarketId())) {
                                listProfitModel.get(i).setRevenue(mapTotal.get(listProfitModel.get(i).getMarketId()));
                                listProfitModel.get(i).setProfit(mapTotalProfit.get(listProfitModel.get(i).getMarketId()));
                            }
                        }
                    } else {
                        // lấy ngày hiện tại
                        Date date = new Date();
                        date.setTime(listProfitModel.get(i).getDate());
                        String today = formatter.format(new Date());
                        String strDate = formatter.format(date);
                        if (today.equals(strDate) && mapTotal.containsKey(listProfitModel.get(i).getMarketId())) {
                            listProfitModel.get(i).setRevenue(mapTotal.get(listProfitModel.get(i).getMarketId()));
                            listProfitModel.get(i).setProfit(mapTotalProfit.get(listProfitModel.get(i).getMarketId()));
                        }
                    }
                }
                adapter.setList(listProfitModel);
            }
        });
    }

    private List<String> getListDateAgo() {
        ArrayList<String> listDate = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        listDate.add(formatter.format(calendar.getTime()));
        int number = 6;
        while (number > 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            number--;
            String today = formatter.format(calendar.getTime());
            listDate.add(today);
        }
        return listDate;
    }

    private void getRevenue(ProductCategories productCategory, Map<String, String> mapTotal) {
        if (mapTotal.containsKey(productCategory.getId_markets())) {
            double price = getPrice(mapTotal.get(productCategory.getId_markets())) * productCategory.getCount() +
                    getPrice(productCategory.getPrice()) * productCategory.getCount();
            mapTotal.put(productCategory.getId_markets(), String.valueOf(price));
        } else {
            double price = getPrice(productCategory.getPrice()) * productCategory.getCount();
            mapTotal.put(productCategory.getId_markets(), String.valueOf(price));
        }
    }

    private void getProfit(ProductCategories productCategory, Map<String, String> mapTotal) {
        if (mapTotal.containsKey(productCategory.getId_markets())) {
            double price = getPrice(mapTotal.get(productCategory.getId_markets()))* productCategory.getCount() +
                    getPriceProfit(productCategory.getPrice(), productCategory.getPriceDefault()) * productCategory.getCount();
            mapTotal.put(productCategory.getId_markets(), String.valueOf(price));
        } else {
            double price = getPriceProfit(productCategory.getPrice(), productCategory.getPriceDefault()) * productCategory.getCount();
            mapTotal.put(productCategory.getId_markets(), String.valueOf(price));
        }
    }

    public double getPrice(String price) {
        return Double.parseDouble(price);
    }

    public double getPriceProfit(String price, String priceDefault) {
        return Double.parseDouble(price) - Double.parseDouble(priceDefault);
    }
}
