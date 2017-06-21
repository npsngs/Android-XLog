package com.forthe.xhttp.http;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class HRequest {
    /*****************************
     * 加载模式
     *****************************/

    /**
     * 加载网络数据(default)
     */
    public static final int LOAD_REMOTE =       0x0000;
    /**
     * 加载缓存数据,如果不存在缓存则加载网络数据
     */
    public static final int LOAD_LOCAL =        0x0001;
    /**
     * 先加载网络数据，然后再加载缓存数据
     */
    public static final int LOAD_LOCAL_REMOTE = 0x0002;


    private String host;
    private final String url;
    private boolean isPost = false;
    private Params params;
    private int bodyType = HttpUtils.FORM_BODY;
    private boolean isNeedCache = false;
    private int loadMode = LOAD_REMOTE;
    private List<String> forbidParams;

    private long cacheValidation = -1;
    private List<String> uploads;
    //use in Mix mode
    private String fieldName;
    //cache key
    private String cacheKey;
    private ParamSupplier paramSupplier;
    private HRequest(String url) {
        super();
        this.url = url;
    }

    public static HRequest create(String url){
        HttpUtils.checkConfig();
        return new HRequest(url);
    }

    public HRequest setHost(String host) {
        this.host = host;
        return this;
    }

    public HRequest post(){
        this.isPost = true;
        this.bodyType = HttpUtils.FORM_BODY;
        return this;
    }
    
    public HRequest post(boolean isForm){
        this.isPost = true;
        this.bodyType = isForm?HttpUtils.FORM_BODY:HttpUtils.NORMAL_BODY;
        return this;
    }

    public HRequest setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    public HRequest setBodyType(int bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public HRequest addParam(String key, Object value){
        if(null == params){
            params = Params.optain();
        }
        params.add(key, value);
        return this;
    }

    public HRequest addForbidParam(String key){
        if(null == forbidParams){
            forbidParams = new ArrayList<>(1);
        }
        forbidParams.add(key);
        return this;
    }

    
    public String getFieldName() {
        return fieldName;
    }

    public HRequest setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public HRequest setParams(Params params) {
        this.params = params;
        return this;
    }
    
    public HRequest setUploadFiles(List<String> files) {
        this.isPost = true;
        this.bodyType = HttpUtils.MULTIPART_BODY;
        this.uploads = files;
        return this;
    }
    
    public HRequest addUploadFile(String file) {
        this.isPost = true;
        this.bodyType = HttpUtils.MULTIPART_BODY;
        if(null == uploads){
            uploads = new ArrayList<>();
        }
        this.uploads.add(file);
        return this;
    }

    public HRequest setParamSupplier(ParamSupplier paramSupplier) {
        this.paramSupplier = paramSupplier;
        return this;
    }

    public HRequest setLoadMode(int loadMode) {
        this.loadMode = loadMode;
        return this;
    }

    public int getLoadMode() {
        return loadMode;
    }

    public HRequest setNeedCache() {
        this.isNeedCache = true;
        return this;
    }

    public boolean isNeedCache(){
        return loadMode == LOAD_LOCAL || loadMode == LOAD_LOCAL_REMOTE || isNeedCache;
    }

    public HRequest setCacheValidation(long validation) {
        this.cacheValidation = validation;
        return this;
    }

    public HResponse send(){
        if(null == host){
            host = HttpUtils.getHost();
        }
        
        if(isNeedCache() && null == cacheKey){
            cacheKey = appendParams();
        }

        if(null == paramSupplier){
            params = HttpUtils.supplyParams(params, !isPost);
        }else{
            params = paramSupplier.supply(params, !isPost);
        }

        if(null != forbidParams && forbidParams.size() > 0){
            for(String forbidParam:forbidParams){
                params.remove(forbidParam);
            }
        }

        String fullUrl = HttpUtils.fullUrl(host, url);
        HResponse response;
        
        if(!isPost){
            if(null != params){
                fullUrl = fullUrl+"?"+params.paramsToString();
            }
            
            response = HttpUtils.doHttpGet(fullUrl);
        }else{
            if(HttpUtils.MULTIPART_BODY != bodyType){
                response = HttpUtils.doHttpPost(fullUrl, params , HttpUtils.FORM_BODY==bodyType);
            }else{
                response = HttpUtils.doHttpUpload(fullUrl, params, uploads);
            }
        }
        
        if(null != response){
            response.sethRequest(this);
            HttpUtils.printLog(response);
        }else{
            HttpUtils.printLog(true, "[no response]"+fullUrl);
        }
        
        return response;
    }
    
    public HResponse restoreCache(){
        HttpCache cache = HttpUtils.getCache();
        if(null != cache){
            if(null == host){
                host = HttpUtils.getHost();
            }
            if(null == cacheKey){
                cacheKey = appendParams();
            }
            long validTime = cacheValidation;
            if(validTime < 0){
                validTime = Long.MAX_VALUE;
            }
            String cacheContent = cache.loadCache(cacheKey, validTime);
            HResponse resp;
            if(TextUtils.isEmpty(cacheContent)){
                resp = new HResponse(HResponse.ERR_CODE_NOCACHE, "have no cache");
            }else{
                resp = new HResponse(HResponse.CODE_FROMCACHE, cacheContent);
            }
            resp.sethRequest(this);
            return resp;
        }
        return new HResponse(HResponse.ERR_CODE_NOCACHE, "have no cache");
    }



    
    public void saveCache(HResponse response){
        HttpCache cache = HttpUtils.getCache();
        if(null != cache && 200 == response.getCode()){
            if(null == host){
                host = HttpUtils.getHost();
            }
            if(null == cacheKey){
                cacheKey = appendParams();
            }
            cache.saveCache(cacheKey, response.getResp());
        }
    }
    
    
    private String appendParams(){
        String fullUrl = HttpUtils.fullUrl(host, url);
        if(null != params){
            fullUrl = fullUrl+"?"+params.paramsToString();
        }
        return fullUrl;
    }

    @Override
    public String toString() {
        if(null == host){
            host = HttpUtils.getHost();

        }
        return (isPost ? "POST\t" : "GET\t") +
                "[" + url + "]\t" +
                String.format("[url:%s]\n", appendParams());
    }
    
}
