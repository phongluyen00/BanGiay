package com.example.retrofitrxjava;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Keep
public class Product implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int idProduct;
    @ColumnInfo
    private String documentId;
    @ColumnInfo
    private String design;
    @ColumnInfo
    private String image;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String price;
    @ColumnInfo
    private String size;
    @ColumnInfo
    private int count;
    @ColumnInfo
    private String sex;
    @ColumnInfo
    private String description;
    @ColumnInfo
    private int idUserModel;
}
