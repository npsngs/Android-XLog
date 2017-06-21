/**---------------------------------------------------------------------
 * Utility: WebUtils
 * Description: 网络工具，提供各种和网络操作相关的方法
 * 
 * Author: DuRuixue
 * Date: 2013-8-5
 * Modifier:hecc
 * Date: 2015-1-9
------------------------------------------------------------------------ */

package com.forthe.xhttp.http;

import android.util.Log;


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpUtils {
    
    public static final int FORM_BODY = 1;
    public static final int NORMAL_BODY = 2;
    public static final int MULTIPART_BODY = 3;

    private static HttpConfig config = null;

    public static void initConfig(HttpConfig config) {
        HttpUtils.config = config;
    }

    private static OkHttpClient getClient() {
        checkConfig();
        return config.client;
    }

    private static boolean isDebugOn() {
        checkConfig();
        return config.isNeedDebug;
    }
    
    public static String getHost() {
        checkConfig();
        return config.httpHost.getHost();
    }
    
    public static HttpCache getCache() {
        if(null != config){
            return config.cache;
        }
        return null;
    }

    public static Params supplyParams(Params params, boolean isValueNeedEncode) {
        if (null != config.paramSupplier) {
            return config.paramSupplier.supply(params, isValueNeedEncode);
        }
        return params;
    }

    public static void handleError(int errorCode){
        checkConfig();
        if(null != config.errorHandler){
            config.errorHandler.onError(errorCode);
        }
    }


    public static void printLog(HResponse response){
        if(null != config && null != config.httpLogger){
            boolean isError = true;
            if(HResponse.CODE_FROMCACHE ==response.getCode() 
             || 200 ==response.getCode()){
                isError = false;
            }
            config.httpLogger.printLog(isError, response.toString());
        }
    }
    
    public static void printLog(boolean isErr, String log) {
        if (null != config.httpLogger) {
            config.httpLogger.printLog(isErr, log);
        } else {
            if (isErr) {
                Log.e("Http ERROR", log);
            } else {
                Log.d("Http DEBUG", log);
            }
        }
    }

    public static void checkConfig() {
        if (null == config) {
            throw new IllegalStateException("you need call initConfig() first");
        }
    }

    public static String fullUrl(String host, String url) {
        if (host.endsWith("/") && url.startsWith("/")) {
            return host.concat(url.substring(1));
        } else if (!host.endsWith("/") && !url.startsWith("/")) {
            return host + "/" + url;
        } else {
            return host + url;
        }
    }

    public static HResponse doHttpGet(String url) {
        HResponse hresp = new HResponse();
        OkHttpClient client = getClient();
        try {
            Request request = new Request.Builder()
                    .header("User-Agent", "Youxiduo-Android")
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if(isDebugOn()){
                printLog(false, printOkHttp(request  ,response));
            }
            
            hresp.setCode(response.code());
            if (200 == response.code()) {
                hresp.setResp(response.body().string());
            } else {
                hresp.setResp("failed by " + hresp.getCode());
            }
        } catch (IOException e) {
            hresp.setCode(HResponse.ERR_CODE_IO);
            hresp.setResp(e.getMessage());
        }
        return hresp;
    }

    public static HResponse doHttpPost(String url, Params params, boolean isUseForm) {
        HResponse hresp = new HResponse();
        OkHttpClient client = getClient();
        try {
            Request request = new Request.Builder()
                    .header("User-Agent", "Youxiduo-Android")
                    .url(url)
                    .post(getRequestBody(params, isUseForm))
                    .build();
            
            Response response = client.newCall(request).execute();
            if(isDebugOn()){
                printLog(false, printOkHttp(request  ,response));
            }
            
            hresp.setCode(response.code());
            if (200 == response.code()) {
                hresp.setResp(response.body().string());
            } else {
                hresp.setResp("failed by " + hresp.getCode());
            }
        } catch (IOException e) {
            hresp.setCode(HResponse.ERR_CODE_IO);
            hresp.setResp(e.getMessage());
        }
        return hresp;
    }

    public static HResponse doHttpUpload(String url,Params params, List<String> files){
        HResponse hresp = new HResponse();
        OkHttpClient client = getClient();
        try {
            Request request = new Request.Builder()
                    .header("User-Agent", "Youxiduo-Android")
                    .url(url)
                    .post(getUploadBody(params, files))
                    .build();
            Response response = client.newCall(request).execute();
            if(isDebugOn()){
                printLog(false, printOkHttp(request  ,response));
            }
            
            hresp.setCode(response.code());
            if (200 == response.code()) {
                hresp.setResp(response.body().string());
            } else {
                hresp.setResp("failed by " + hresp.getCode());
            }
        } catch (IOException e) {
            hresp.setCode(HResponse.ERR_CODE_IO);
            hresp.setResp(e.getMessage());
        }
        return hresp;
    }
    
    private static RequestBody getRequestBody(Params params, boolean isUseForm) {
        if (isUseForm) {
            FormBody.Builder builder = new FormBody.Builder();
            List<String> sortKeys = params.sortKeys();
            for (String key : sortKeys) {
                String value = params.getValue(key);
                builder.add(key, value);
            }
            return builder.build();
        } else {
            String json ;
            try{
                JSONObject jsonObject = new JSONObject();
                List<String> sortKeys = params.sortKeys();
                for (String key : sortKeys) {
                    String value = params.getValue(key);
                    jsonObject.put(key, value);
                }
                json = jsonObject.toString(); 
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
            return RequestBody.create(MediaType.parse("application/json"), json);
        }
    }

    private static RequestBody getUploadBody(Params params, List<String> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        List<String> sortKeys = params.sortKeys();
        for (String key : sortKeys) {
            String value = params.getValue(key);
            builder.addFormDataPart(key, value);
        }
        MediaType mediaType = MediaType.parse("*");
        for(String file:files){
            File f = new File(file);
            if(f.exists()){
                builder.addFormDataPart("uploadfile", f.getName(), RequestBody.create(mediaType, f));
            }
        }
        
        return builder.build();
    }
    
    private static String printOkHttp(Request request, Response response){
        StringBuilder builder = new StringBuilder("OKHTTP DEBUG");
        builder.append("[URL]:").append(request.url().toString()).append("\n");
        builder.append("[REQUEST]:\n--------------\n");
        builder.append(request.method()).append("\n");
        builder.append(request.headers()).append("\n");
        builder.append("[RESPONSE]:\n--------------\n");
        builder.append(response.headers()).append("\n");
        return builder.toString();
    }


    public static String mapErrorMessage(int errorCode, String errMsg) {
        switch (errorCode) {
            case HResponse.ERR_CODE_IO:
            case HResponse.ERR_CODE_NET:
                return "无法连接到服务器，请检查网络链接";
            case HResponse.ERR_CODE_JSON:
            case HResponse.ERR_CODE_UNKNOWN:
                return "" +
                        "未知错误";
            default:
                return errMsg;
        }
    }
    
}
