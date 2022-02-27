package com.example.retrofitrxjava;

public interface ItemFavoriteListener<T> {
    void onClickProduct(T t);
    void onClickRemove(T t);
}
