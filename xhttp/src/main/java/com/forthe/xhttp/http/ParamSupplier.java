package com.forthe.xhttp.http;

public interface ParamSupplier {
    Params supply(Params src, boolean isValueNeedEncode);
}
