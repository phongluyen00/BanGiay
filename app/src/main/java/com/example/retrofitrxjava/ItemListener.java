package com.example.retrofitrxjava;

public interface ItemListener<T> {
    void onEditProduct(T t, int position);
    void onDeleteProduct(T t, int position);
    void onClickProduct(T t);
}
