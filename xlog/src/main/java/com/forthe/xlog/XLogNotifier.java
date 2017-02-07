package com.forthe.xlog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.forthe.xlog.core.LogNotifier;

abstract class XLogNotifier implements LogNotifier{
    private Handler handler = null;
    XLogNotifier(){
        if(handler == null){
            handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case 0:
                            onNotifyLogAdd((String) msg.obj);
                            break;
                        case 1:
                            onNotifyLogClear();
                            break;
                    }
                }
            };
        }
    }

    @Override
    public void onLogAdd(String log) {
        handler.obtainMessage(0, log).sendToTarget();
    }

    @Override
    public void onLogClear() {
        handler.obtainMessage(1).sendToTarget();
    }


    protected abstract void onNotifyLogAdd(String log);
    protected abstract void onNotifyLogClear();

}
