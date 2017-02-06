package com.forthe.xlog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

abstract class XLogReceiver {
    private Handler handler = null;
    private final Object mReadyFence = new Object();
    void init(){
        synchronized (mReadyFence) {
            if (handler == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        synchronized (mReadyFence) {
                            handler = new MYHandler(Looper.myLooper());
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

    class MYHandler extends Handler{
        MYHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onReceiveLog((String) msg.obj);
        }
    }
    void receiveLog(String log){
        if (handler != null) {
            handler.obtainMessage(0,log).sendToTarget();
        }
    }

    abstract void onReceiveLog(String log);
}
