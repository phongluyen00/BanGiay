package com.example.retrofitrxjava.model;

import java.io.Serializable;

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
    private String type;
    private String documentId;
    private String price;
    private String vote;
    private int count;
    private String description;
    private String id_document;
    private String uid;
    private boolean isFavorite;
}
