package com.example.retrofitrxjava.activity;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.constanst.Constants;
import com.example.retrofitrxjava.databinding.BottomSheetKqkdBinding;
import com.example.retrofitrxjava.model.ProductCategories;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class BusinessReportActivity extends AppCompatAct<BottomSheetKqkdBinding> {
    private ArrayList<ProductCategories> listProductCategories;

    @Override
    protected void initLayout() {
        listProductCategories = new ArrayList<>();
        bd.back.setOnClickListener(v -> onBackPressed());
        getAllCart();
    }

    @Override
    protected int getID() {
        return R.layout.bottom_sheet_kqkd;
    }

    private void getAllCart() {
        db.collection(Constants.KEY_CART)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    ProductCategories productCategories = documentSnapshot.toObject(ProductCategories.class);
                    listProductCategories.add(productCategories);
                }
                getRevenue();
                getProfit();
                getResultTh();
                getProductgvth();
            }
        });
    }

    private void getRevenue() {
        double price = 0;
        double priceDefaultTotal = 0;
        for (ProductCategories listProductCategory : listProductCategories) {
            if (listProductCategory.getStatus() == Constants.KEY_ITEM_COMPLETED) {
                price += getPrice(listProductCategory.getPrice()) * listProductCategory.getCount();
                priceDefaultTotal += getPrice(listProductCategory.getPriceDefault())
                        * listProductCategory.getCount();
            }
        }
        bd.ban.setText(formatPrice(price));
        bd.resultGvbh.setText(formatPrice(priceDefaultTotal));
    }

    private void getProfit() {
        double profit = 0;
        for (ProductCategories listProductCategory : listProductCategories) {
            if (listProductCategory.getStatus() == Constants.KEY_ITEM_COMPLETED) {
                profit += getPriceProfit(listProductCategory.getPrice(), listProductCategory.getPriceDefault())
                        * listProductCategory.getCount();
            }
        }
        bd.resultTotal.setText(formatPrice(profit));
    }

    private void getResultTh() {
        double price = 0;
        for (ProductCategories listProductCategory : listProductCategories) {
            if (listProductCategory.getStatus() == Constants.KEY_ITEM_CANCEL) {
                price += getPrice(listProductCategory.getPrice()) * listProductCategory.getCount();
            }
        }
        bd.resultTh.setText(String.valueOf(price));
    }

    private void getProductgvth() {
        double profitCancel = 0;
        for (ProductCategories listProductCategory : listProductCategories) {
            if (listProductCategory.getStatus() == Constants.KEY_ITEM_CANCEL) {
                profitCancel += getPriceProfit(listProductCategory.getPrice(), listProductCategory.getPriceDefault())
                        * listProductCategory.getCount();
            }
        }
        bd.gvth.setText(String.valueOf(profitCancel));
    }

    public double getPrice(String price) {
        return Double.parseDouble(price);
    }

    public double getPriceProfit(String price, String priceDefault) {
        return Double.parseDouble(price) - Double.parseDouble(priceDefault);
    }
}
