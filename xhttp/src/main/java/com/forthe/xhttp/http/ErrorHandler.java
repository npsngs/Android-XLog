package com.forthe.xhttp.http;

/**
 * @author Hecc
 * Handle error by each errorCode
 */
public interface ErrorHandler {
    void onError(int errorCode);
}
