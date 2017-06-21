package com.forthe.xhttp.network;
import org.json.JSONObject;

import java.util.List;

public abstract class NetworkListScheduler<T> extends NetworkJsonScheduler {
    
    @Override
    protected final void onResponse(JSONObject result, boolean isFromCache) throws Exception {
        if (isResultOK(result)){
            parse(result, isFromCache);
        } else if(!isFromCache){
            parseError(result);
        }
    }

    protected abstract void parse(JSONObject jsonObject, boolean isFromCache) throws Exception;
    protected abstract void onSuccess(List<T> datas, boolean hasMore, boolean isFromCache);
    
}
