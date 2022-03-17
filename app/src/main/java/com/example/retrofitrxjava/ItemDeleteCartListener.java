package com.example.retrofitrxjava;

public interface ItemDeleteCartListener<T> {
    void onItemDeleteClick(T t, int index);
    void onItemAddClick(T t, int index);
}
