package com.forthe.xhttp.http;

public interface HttpCache {
    String loadCache(String url, long validation);
    void saveCache(String url, String content);
}
