package com.forthe.xhttp.network;
import org.json.JSONObject;

/**
 * 最简单的网络调用,即：<p>
 * 返回errorCode = 0 且  存在result 为成功，其余为失败
 * 
 */
public abstract class NetworkSimpleScheduler extends NetworkJsonScheduler {
    
    @Override
    protected final void onResponse(JSONObject result, boolean isFromCache) throws Exception {
        if (isResultOK(result)){
            onSuccess();
        } else if(!isFromCache){
            parseError(result);
        }
    }
    protected abstract void onSuccess();
    
}
