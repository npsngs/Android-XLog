package com.forthe.xlog.core;

public interface LogStore {
    void storeLog(String log);
    void clear();
}
