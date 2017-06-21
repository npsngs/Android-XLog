package com.forthe.xhttp.network;
import org.json.JSONObject;

/**
 * @author hecc
 * 封装了返回单一数据对象的网络接口异步调用 
 * @param <T> 有接口返回的对象类型
 */
public abstract class NetworkSingleScheduler<T> extends NetworkJsonScheduler {
    
    @Override
    protected final void onResponse(JSONObject result, boolean isFromCache) throws Exception {
        if (isResultOK(result)){
            JSONObject jsonObj;
            if(isResultJson()){
                jsonObj = result.getJSONObject("result");
            }else{
                jsonObj = result;
            }
            
            T t = parse(jsonObj);
            onSuccess(t, isFromCache);
        } else if(!isFromCache){
            parseError(result);
        }
    }

    /**
     * @return 返回值决定了parse(JSONObject result)方法的result参数
     *  TRUE    :result 为返回json中的result jsonObject(默认)
     *  FALSE   :result 为返回完整json
     */
    protected boolean isResultJson(){
        return true;
    }
    
    /**
     * 
     * @param result 接口返回json<p>
     * {@link boolean isResultJson()}
     */
    protected abstract T parse(JSONObject result) throws Exception;
    protected abstract void onSuccess(T t, boolean isFromCache);
    
}
