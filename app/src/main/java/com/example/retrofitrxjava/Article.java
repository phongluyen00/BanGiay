package com.example.retrofitrxjava;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Article implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String idArticle;
    @ColumnInfo
    private int idUserModel;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private String location;
    @ColumnInfo
    private String date;
    @ColumnInfo
    private String thumb;
    @ColumnInfo
    private String des;
    @ColumnInfo
    private String link;
    @ColumnInfo
    private String income;
    @ColumnInfo(name = "name_of_pages")
    private String numberOfPages;
    @ColumnInfo
    private String size;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo
    private String price;
    @ColumnInfo
    private String translator;
    @ColumnInfo(name = "publishing_company")
    private String publishingCompany;
    @ColumnInfo
    private String author;
    @ColumnInfo
    private int count;
    private String type;
}