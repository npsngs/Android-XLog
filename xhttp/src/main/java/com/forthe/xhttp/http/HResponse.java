package com.forthe.xhttp.http;


public class HResponse {
    public static final int ERR_CODE_NET =      -98;
    public static final int ERR_CODE_IO =       -99;
    public static final int ERR_CODE_JSON =     -100;
    public static final int ERR_CODE_UNKNOWN =  -101;
    public static final int ERR_CODE_NOCACHE =  -1000;
    public static final int CODE_FROMCACHE =    1000;
    private HRequest hRequest;
    private int code;
    private String resp;
    private Object data;
    private boolean canCache = false;
    
    public HResponse(int code, String resp) {
        super();
        this.code = code;
        this.resp = resp;
    }

    public HResponse() {
        super();
    }

    public HRequest gethRequest() {
        return hRequest;
    }

    public void sethRequest(HRequest hRequest) {
        this.hRequest = hRequest;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public boolean isCanCache() {
        return canCache;
    }

    public void setCanCache(boolean canCache) {
        this.canCache = canCache;
    }

    public void saveCache(){
        if(null != hRequest && canCache){
            hRequest.saveCache(this);
            canCache = false;
        }
    }
    
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(null!=hRequest?hRequest.toString():"");
        builder
        .append("[response:").append(code).append("]:")
        .append(null!=resp?resp:"");
        return builder.toString();
    }
}
