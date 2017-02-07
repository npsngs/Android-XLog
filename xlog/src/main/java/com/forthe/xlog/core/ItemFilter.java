package com.forthe.xlog.core;

public interface ItemFilter<T> {
    boolean filter(T item);
}
