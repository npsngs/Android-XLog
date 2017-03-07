package com.forthe.xlog;

import android.content.Context;
import android.text.TextUtils;

import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.XLogNotifier;
import com.forthe.xlog.view.LogAdapter;

class XLogAdapter extends LogAdapter {
    private XLogNotifier logNotifier;
    XLogAdapter(Context mContext, Container container) {
        super(mContext, container);
        setData(XLog.getLogs());
        logNotifier = new XLogNotifier() {
            @Override
            protected void onNotifyLogAdd(String log) {
                addData(log);
            }

            @Override
            protected void onNotifyLogClear() {
                clear();
            }
        };
        XLog.setLogNotifier(logNotifier);
    }

    @Override
    protected boolean onFilterTag(String tag, String item) {
        return item.startsWith(tag);
    }

    @Override
    protected String getTypeTag(String log) {
        if(TextUtils.isEmpty(log)){
            return "";
        }
        return log.substring(0,1);
    }


}
