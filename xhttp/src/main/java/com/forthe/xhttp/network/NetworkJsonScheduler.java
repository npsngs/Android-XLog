package com.forthe.xhttp.network;

import com.forthe.xhttp.http.HResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author hecc
 * 封装了一个网络访问的异步调用
 */
public abstract class NetworkJsonScheduler extends NetworkScheduler {
    /**
     * 返回结果的处理(UI线程中）
     * 建议代码<p>
     * void onResponse(result, isFromCache){<p>
     * { if(isResultOK(result)){<p>
     *          你的代码...........
     *      }else{<p>
     *          parseError(result);<p>
     *      }<p>
     * }<p>
     */
    protected abstract void onResponse(JSONObject result, boolean isFromCache) throws Exception;

    @Override
    protected void onReturnResp(HResponse resp) {
        if(200 == resp.getCode() || HResponse.CODE_FROMCACHE == resp.getCode()){
            try{
                JSONObject result = (JSONObject) resp.getData();
                onResponse(result, 200 != resp.getCode()) ;
            }catch(Exception e){
                handleError(HResponse.ERR_CODE_UNKNOWN,e.getMessage());
                e.printStackTrace();
            }
        }else if(HResponse.ERR_CODE_NOCACHE != resp.getCode()){
            handleError(resp.getCode(), resp.getResp());
        }
    }


    protected void parseResp(HResponse resp){
        if(200 == resp.getCode() || HResponse.CODE_FROMCACHE == resp.getCode()){
            try{
                JSONObject result = new JSONObject(new JSONTokener(resp.getResp()));
                resp.setData(result);
            }catch(Exception e){
                try {
                    JSONArray array = new JSONArray(new JSONTokener(resp.getResp()));
                    JSONObject result = new JSONObject();
                    result.put("errorCode", "0");
                    result.put("result", array);
                    resp.setData(result);
                }catch (Exception e1){
                    e.printStackTrace();
                    e1.printStackTrace();
                    if(HResponse.CODE_FROMCACHE == resp.getCode()){
                        resp.setCode(HResponse.ERR_CODE_NOCACHE);
                    }else{
                        resp.setCode(HResponse.ERR_CODE_JSON);
                    }
                }
            }
        }
    }

    protected void parseError(JSONObject result) throws Exception{
        int errCode = 1;
        try{
            errCode = result.getInt("errorCode");
        }catch (Exception e){
            e.printStackTrace();
        }

        String errMsg = "error by server";
        if (result.has("errorMessage")) {
            errMsg = result.getString("errorMessage");
        }else if(result.has("errorDescription")){
            errMsg = result.getString("errorDescription");
        }else if(result.has("message")){
            errMsg = result.getString("message");
        }else if(result.has("error_description")){
            errMsg = result.getString("error_description");
        }

        handleError(errCode, errMsg);
    }
    
    protected boolean isResultOK(JSONObject result) throws Exception{
        return result.has("result") && result.getInt("errorCode") == 0;
    }


    protected boolean isEmptyValue(JSONObject result, String key){
        try{
            if(result.has(key)){
                if(!result.isNull(key)){
                    String value = result.getString(key);
                    return "[]".equals(value) || "{}".equals(value);
                }else {
                    return true;
                }
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }
}
