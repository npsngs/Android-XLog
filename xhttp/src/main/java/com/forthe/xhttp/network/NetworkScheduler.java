package com.forthe.xhttp.network;


import com.forthe.xhttp.http.HRequest;
import com.forthe.xhttp.http.HResponse;
import com.forthe.xhttp.http.HttpUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author hecc
 * 封装了一个网络访问的异步调用,不能直接使用
 */
public abstract class NetworkScheduler {
    protected abstract void onReturnResp(HResponse resp);
    protected abstract void parseResp(HResponse resp);
    protected abstract void onFailed(int errorCode, String errorMsg);


    protected void sendRequest(HRequest request) {
        Subscriber<HResponse> subscriber = createSubscriber();
        switch(request.getLoadMode()){
            case HRequest.LOAD_LOCAL:
                Observable
                        .concat(createCacheObservable(request), createHttpObservable(request))
                        .takeUntil(new Func1<HResponse, Boolean>() {
                            @Override
                            public Boolean call(HResponse t) {
                                if(HResponse.CODE_FROMCACHE == t.getCode()){
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
                        .concat(createCacheObservable(request), createHttpObservable(request))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                break;
            default:
                createHttpObservable(request)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
                break;
        }
    }

    private Subscriber<HResponse> createSubscriber() {
        return new Subscriber<HResponse>() {
            @Override
            public void onNext(HResponse resp) {
                onReturnResp(resp);
            }
            
            @Override
            public void onError(Throwable e) {
                handleError(HResponse.ERR_CODE_UNKNOWN, e.getMessage());
            }
            
            @Override
            public void onCompleted() {
            }
        };
    }


    protected void handleError(int errorCode, String errorMsg){
        HttpUtils.handleError(errorCode);
        onFailed(errorCode, HttpUtils.mapErrorMessage(errorCode, errorMsg));
    }

    private Observable<HResponse> createCacheObservable(HRequest req){
        return Observable
                .just(req)
                .map(new Func1<HRequest, HResponse>() {
                    @Override
                    public HResponse call(HRequest t) {
                        return t.restoreCache();
                    }
                })
                .filter(new Func1<HResponse, Boolean>() {
                    @Override
                    public Boolean call(HResponse t) {
                        parseResp(t);
                        return HResponse.ERR_CODE_NOCACHE != t.getCode();
                    }
                })
                .subscribeOn(Schedulers.io());
    }
    
    
    private Observable<HResponse> createHttpObservable(HRequest req){
        Observable<HResponse> observable = Observable
                .just(req)
                .map(new Func1<HRequest, HResponse>() {
                    @Override
                    public HResponse call(HRequest t) {
                        HResponse response = t.send();
                        parseResp(response);
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io());
        if(req.isNeedCache()){
            observable = observable
            .flatMap(new Func1<HResponse, Observable<HResponse>>() {
                @Override
                public Observable<HResponse> call(HResponse t) {
                    return Observable.just(t).repeat(2);
                }
            })
            .filter(new Func1<HResponse, Boolean>() {
                @Override
                public Boolean call(HResponse t) {
                    if(t.isCanCache()){
                        t.saveCache();
                        return false;
                    }else{
                        t.setCanCache(true);
                        return true;
                    }
                }
            });
        }
        return observable;
    }

}
