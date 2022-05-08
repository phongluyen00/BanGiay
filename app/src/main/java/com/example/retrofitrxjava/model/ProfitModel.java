package com.example.retrofitrxjava.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProfitModel {
    private String nameProduct;
    private String profit;
    private String marketId;
    private String revenue;
    private long date;

    public ProfitModel(String nameProduct, String profit, String revenue) {
        this.nameProduct = nameProduct;
        this.profit = profit;
        this.revenue = revenue;
    }
}
