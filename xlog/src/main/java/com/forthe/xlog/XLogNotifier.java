package com.forthe.xlog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class XLogNotifier {
    private Handler handler = null;
    private void init(){
        if(handler == null){
            handler = new NotifyHandler(Looper.getMainLooper());
        }
    }

    class NotifyHandler extends Handler{
        NotifyHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            if(onLogChangeListener != null){
                switch (msg.what){
                    case 0:
                        onLogChangeListener.onLogAdded((String) msg.obj);
                        break;
                    case 1:
                        onLogChangeListener.onLogClear();
                        break;
                }
            }
        }
    }

    private XLog.OnLogChangeListener onLogChangeListener = null;
    void setOnLogChangeListener(XLog.OnLogChangeListener onLogChangeListener) {
        this.onLogChangeListener = onLogChangeListener;
        init();
    }

    void onNotifyLogAdd(String log){
        if(onLogChangeListener == null || handler == null){
            return;
        }
        handler.obtainMessage(0, log).sendToTarget();
    }

    void onNotifyLogClear(){
        if(onLogChangeListener == null || handler == null){
            return;
        }
        handler.obtainMessage(1).sendToTarget();
    }
}
