package com.example.retrofitrxjava.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Markets implements Serializable {
    private String image;
    private String title;
    private String status;
    private String documentId;

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getDocumentId() {
        return documentId;
    }
}
