package com.example.retrofitrxjava;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelManager {
    private String documentId;
    private String code;
    private String name;
    private String in_debt;
    private String have;
}
