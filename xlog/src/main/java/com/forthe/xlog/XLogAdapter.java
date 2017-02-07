package com.forthe.xlog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.forthe.xlog.frame.ColorPool;
import com.forthe.xlog.frame.FilterAdapter;
import com.forthe.xlog.frame.PanelContainer;
import com.forthe.xlog.panel.TextPanel;
import com.forthe.xlog.tools.XLogUtils;


class XLogAdapter extends FilterAdapter<String>{
    private PanelContainer panelContainer;
    XLogAdapter(Context mContext, PanelContainer panelContainer) {
        super(mContext);
        setData(XLog.getLogs());
        this.panelContainer = panelContainer;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogHolder logHolder;
        if(null == convertView){
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(12f);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setClickable(false);
            tv.setGravity(Gravity.START);
            int padding = XLogUtils.dp2px(getContext(), 10);
            tv.setPadding(padding, padding, padding, padding);
            logHolder = new LogHolder(tv);
            tv.setTag(logHolder);
        }else{
            logHolder = (LogHolder) convertView.getTag();
        }

        logHolder.bind(position);
        return logHolder.tv;
    }

    private class LogHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView tv;
        int position;
        LogHolder(TextView tv) {
            this.tv = tv;
            tv.setOnClickListener(this);
            tv.setOnLongClickListener(this);
        }

        void bind(int position){
            this.position = position;

            String log = getItem(position);
            if(log.startsWith("E")){
                tv.setTextColor(ColorPool.e_color);
            }else if(log.startsWith("W")){
                tv.setTextColor(ColorPool.w_color);
            }else if(log.startsWith("D")){
                tv.setTextColor(ColorPool.d_color);
            }

            if(!TextUtils.isEmpty(log) && log.length() > 200){
                log = log.substring(0, 180);
                log = log+"...";
            }
            tv.setText(log);
        }

        @Override
        public void onClick(View v) {
            if(null != panelContainer){
                String log = getItem(position);
                if(log.startsWith("E")){
                    panelContainer.showPanel(new TextPanel(log, ColorPool.e_color));
                }else if(log.startsWith("W")){
                    panelContainer.showPanel(new TextPanel(log, ColorPool.w_color));
                }else if(log.startsWith("D")){
                    panelContainer.showPanel(new TextPanel(log, ColorPool.d_color));
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            String text = getItem(position);
            XLogUtils.sendText(getContext(), text);
            return true;
        }
    }
}
