
package com.forthe.xhttp.network;


import com.forthe.xhttp.http.HRequest;
import com.forthe.xhttp.http.HResponse;
import com.forthe.xhttp.http.HttpUtils;

import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author hecc
 * 混合多个接口的返回结果（返回结果类型一致）
 */
public abstract class NetworkMixScheduler {
    protected void sendRequest(HRequest... requests) {
        Subscriber<HMixResponse> subscriber = createSubscriber();
        HRequest request = requests[0];
        switch (request.getLoadMode()){
            case HRequest.LOAD_LOCAL:
                Observable
                        .concat(createCacheObservable(requests), createHttpObservable(requests))
                        .takeUntil(new Func1<HMixResponse, Boolean>() {
                            @Override
                            public Boolean call(HMixResponse t) {
                                if (HResponse.CODE_FROMCACHE == t.getCode()) {
                                    return Boolean.TRUE;
                                }
                                return Boolean.FALSE;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                break;
            case HRequest.LOAD_LOCAL_REMOTE:
                Observable
                        .concat(createCacheObservable(requests), createHttpObservable(requests))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                break;
            default:
                createHttpObservable(requests)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                break;
        }
    }

    private Subscriber<HMixResponse> createSubscriber() {
        return new Subscriber<HMixResponse>() {
            @Override
            public void onNext(HMixResponse resp) {
                parseResp(resp);
            }

            @Override
            public void onError(Throwable e) {
                onFailed(HResponse.ERR_CODE_UNKNOWN, HttpUtils.mapErrorMessage(HResponse.ERR_CODE_UNKNOWN, e.getMessage()));
            }

            @Override
            public void onCompleted() {
            }
        };
    }

    private void parseResp(HMixResponse resp) {
        if (200 == resp.getCode() || HResponse.CODE_FROMCACHE == resp.getCode()) {
            try {
                JSONObject result = resp.getResp();
                onResponse(result, 200 != resp.getCode());
            } catch (Exception e) {
                onFailed(HResponse.ERR_CODE_UNKNOWN, HttpUtils.mapErrorMessage(HResponse.ERR_CODE_UNKNOWN, e.getMessage()));
                e.printStackTrace();
            }
        } else {
            if (HResponse.ERR_CODE_NOCACHE != resp.getCode()) {
                onFailed(resp.getCode(), HttpUtils.mapErrorMessage(resp.getCode(), resp.getErrorMsg()));
            }
        }
    }

    protected void parseError(JSONObject result) throws Exception {
        int errCode = result.getInt("errorCode");
        String errMsg = "error by server";
        if (result.has("errorMessage")) {
            errMsg = result.getString("errorMessage");
        } else if (result.has("errorDescription")) {
            errMsg = result.getString("errorDescription");
        }

        onFailed(errCode, errMsg);
    }

    protected boolean isResultOK(JSONObject result) throws Exception {
        return result.has("result") && result.getInt("errorCode") == 0;
    }
    
    /**
     * @param result       多个返回值拼在同一个json中
     * @param isFromCache  是否来自缓存
     */
    protected abstract void onResponse(JSONObject result, boolean isFromCache) throws Exception;
    
    protected abstract void onFailed(int errorCode, String errorMsg);

    private Observable<HMixResponse> createCacheObservable(HRequest... requests) {
        return Observable
                .from(requests)
                .map(new Func1<HRequest, HResponse>() {
                    @Override
                    public HResponse call(HRequest t) {
                        return t.restoreCache();
                    }
                })
                .collect(new Func0<HMixResponse>() {
                    @Override
                    public HMixResponse call() {
                        return new HMixResponse();
                    }
                }, new Action2<HMixResponse, HResponse>() {
                    @Override
                    public void call(HMixResponse t1, HResponse t2) {
                        t1.mix(t2);
                    }
                })
                .filter(new Func1<HMixResponse, Boolean>() {
                    @Override
                    public Boolean call(HMixResponse t) {
                        return HResponse.ERR_CODE_NOCACHE != t.getCode();
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private HMixResponse mixResponse;

    private Observable<HMixResponse> createHttpObservable(HRequest... requests) {
        HRequest request = requests[0];
        mixResponse = new HMixResponse();
        Observable<HMixResponse> observable = Observable
                .from(requests)
                .takeUntil(new Func1<HRequest, Boolean>() {
                    @Override
                    public Boolean call(HRequest t) {
                        return canReturn(mixResponse);
                    }
                })
                .map(new Func1<HRequest, HResponse>() {
                    @Override
                    public HResponse call(HRequest t) {
                        return t.send();
                    }
                })
                .collect(new Func0<HMixResponse>() {
                    @Override
                    public HMixResponse call() {
                        return mixResponse;
                    }
                }, new Action2<HMixResponse, HResponse>() {
                    @Override
                    public void call(HMixResponse t1, HResponse t2) {
                        t1.mix(t2);
                    }
                })
                .subscribeOn(Schedulers.io());
        if (request.isNeedCache()) {
            observable = observable
                    .flatMap(new Func1<HMixResponse, Observable<HMixResponse>>() {
                        @Override
                        public Observable<HMixResponse> call(HMixResponse t) {
                            return Observable.just(t).repeat(2);
                        }
                    })
                    .filter(new Func1<HMixResponse, Boolean>() {
                        @Override
                        public Boolean call(HMixResponse t) {
                            if (t.isCanCache()) {
                                t.saveCache();
                                return false;
                            } else {
                                t.setCanCache(true);
                                return true;
                            }
                        }
                    });
        }
        return observable;
    }

    /**
     * 
     * @param mixResponse 多个接口的数据集合
     * @return TRUE 可以提前返回而不必全部接口加载完成
     */
    protected boolean canReturn(HMixResponse mixResponse) {
        return false;
    }

}
