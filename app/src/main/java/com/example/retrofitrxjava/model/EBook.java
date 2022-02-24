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
    private String documentId, image, title, description, author, categories, price, file_pdf, vote;
    private int percent;
}
