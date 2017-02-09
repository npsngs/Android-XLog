package com.forthe.xlog;

import android.content.Context;
import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.XLogNotifier;
import com.forthe.xlog.view.LogAdapter;

class XLogAdapter extends LogAdapter{
    XLogAdapter(Context mContext, Container container) {
        super(mContext, container);
        setData(XLog.getLogs());
        XLog.setLogNotifier(new XLogNotifier() {
            @Override
            protected void onNotifyLogAdd(String log) {
                addData(log);
            }

            @Override
            protected void onNotifyLogClear() {
                clear();
            }
        });
    }


}
