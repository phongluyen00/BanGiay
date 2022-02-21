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
}
