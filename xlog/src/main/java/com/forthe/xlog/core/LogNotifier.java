package com.forthe.xlog.core;

public interface LogNotifier {
    void onLogAdd(String log);
    void onLogClear();
}
