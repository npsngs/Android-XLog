package com.forthe.xhttp.network;


import com.forthe.xhttp.http.HResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HMixResponse {
    private int code;
    private JSONObject resp;
    private String errorMsg;
    private boolean canCache = false;
    
    private List<HResponse> responses;
    public void mix(HResponse response){
        try{
            if(null == resp){
                resp = new JSONObject();
            }
            if(null == responses){
                responses = new ArrayList<>();
            }
            int respCode = response.getCode();
            if(200 == respCode || HResponse.CODE_FROMCACHE == respCode){
                code = respCode;
                JSONObject part = new JSONObject(response.getResp());
                resp.putOpt(response.gethRequest().getFieldName(), part);
            }else{
                if(200 != code && HResponse.CODE_FROMCACHE != code){
                    code = respCode;
                    errorMsg = response.getResp();
                }
            }
            responses.add(response);
        }catch(Exception e){
            if(200 != code && HResponse.CODE_FROMCACHE != code){
                code = HResponse.ERR_CODE_UNKNOWN;
                errorMsg = e.getMessage();
            }
        }
    }
    public List<HResponse> getResponses() {
        return responses;
    }
    public boolean isCanCache() {
        return canCache;
    }
    public void setCanCache(boolean canCache) {
        this.canCache = canCache;
        if(null != responses && canCache){
            for(HResponse response:responses){
                response.setCanCache(true);
            }
        }
    }
    
    public void saveCache(){
        if(null != responses && canCache){
            for(HResponse response:responses){
                response.saveCache();
                response.setCanCache(false);
            }
            canCache = false;
        }
    }
    public int getCode() {
        return code;
    }
    public JSONObject getResp() {
        return resp;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
}
