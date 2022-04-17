package com.example.retrofitrxjava.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Bill {
    private List<String> list_id_order;
    private String billId;
    private List<ProductCategories> productCategoriesList;
    private Long time;
    private Long timeUpdate;
    private int status;
    private String uid;

    public Map<String, Object> toMapData() {
        Map<String, Object> map = new HashMap<>();
        map.put("list_id_order", list_id_order);
        map.put("billId", billId);
        map.put("time", time);
        map.put("timeUpdate", timeUpdate);
        map.put("status", status);
        map.put("uid", uid);
        return map;
    }
}
