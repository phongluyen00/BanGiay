package com.example.retrofitrxjava.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EBook implements Serializable {
    private String documentId, image, title, description, author, price, file_pdf, vote;
    private int percent;
    private String id_document;
    private String uid;
    private String total_page;
    private int page;
}
