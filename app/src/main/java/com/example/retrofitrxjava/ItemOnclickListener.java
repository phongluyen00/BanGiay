package com.example.retrofitrxjava;

public interface ItemOnclickListener<T> {
    void onItemBookClick(T t, int position);
    void onRemove(T t, int position);
}
