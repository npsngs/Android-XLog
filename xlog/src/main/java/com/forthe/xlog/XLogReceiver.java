package com.forthe.xlog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.forthe.xlog.core.LogReceiver;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

abstract class XLogReceiver implements LogReceiver{
    private final Object mReadyFence = new Object();
    private final CopyOnWriteArrayList earlyCache = new CopyOnWriteArrayList();
    private Handler handler;
    void init(){
        synchronized (mReadyFence) {
            if (handler == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        synchronized (mReadyFence) {
                            handler = new Handler(Looper.myLooper()){
                                @Override
                                public void handleMessage(Message msg) {
                                    onReceiveLog((String) msg.obj);
                                }
                            };

                            if(!earlyCache.isEmpty()){
                                Iterator<String> iterator = earlyCache.iterator();
                                while (iterator.hasNext()){
                                    onReceiveLog(iterator.next());
                                }
                                earlyCache.clear();
                            }
                            mReadyFence.notify();
                        }
                        Looper.loop();
                        synchronized (mReadyFence) {
                            handler = null;
                        }
                    }
                }).start();
            }
        }
    }


    @Override
    public void receiveLog(String log){
        if (handler != null) {
            handler.obtainMessage(0,log).sendToTarget();
        }else{
            earlyCache.add(log);
        }
    }

    protected abstract void onReceiveLog(String log);
}
