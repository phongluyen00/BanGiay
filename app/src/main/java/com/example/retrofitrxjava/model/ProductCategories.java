package com.example.retrofitrxjava.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductCategories implements Serializable {
    private String title;
    private String image;
    private String documentId;
    private String price;
    private int count;
    private String description;
    private String id_document;
    private String uid;
    private String idBill;
    private int status;
    private boolean isFavorite;
    private String id_markets;
    private String quantity;

    public ProductCategories(String title, String image, String price, String description, String id_markets) {
        this.title = title;
        this.image = image;
        this.price = price;
        this.id_markets = id_markets;
        this.description = description;
    }

    public Map<String, Object> toMapData() {
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);
        map.put("image", image);
        map.put("idBill", idBill);
        map.put("price", price);
        map.put("title", title);
        map.put("uid", uid);

        return map;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public String getId_document() {
        return id_document;
    }

    public String getUid() {
        return uid;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public int getStatus() {
        return status;
    }

    public String getId_markets() {
        return id_markets;
    }

    public String totalPrice() {
        return String.valueOf(Double.parseDouble(price) * count);
    }
}
