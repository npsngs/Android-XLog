package com.forthe.xlog.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.ColorPool;
import com.forthe.xlog.frame.FilterAdapter;
import com.forthe.xlog.panel.TextPanel;
import com.forthe.xlog.tools.XLogUtils;


public class LogAdapter extends FilterAdapter<String>{
    protected Container container;
    public LogAdapter(Context mContext, Container container) {
        super(mContext);
        this.container = container;
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
            int padding = XLogUtils.dp2px(getContext(), 6);
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
            }else{
                tv.setTextColor(Color.GRAY);
            }

            if(!TextUtils.isEmpty(log) && log.length() > 200){
                log = log.substring(0, 180);
                log = log+"...";
            }
            tv.setText(log);
        }

        @Override
        public void onClick(View v) {
            if(null != container){
                String log = getItem(position);
                if(log.startsWith("E")){
                    container.showPanel(new TextPanel(log, ColorPool.e_color));
                }else if(log.startsWith("W")){
                    container.showPanel(new TextPanel(log, ColorPool.w_color));
                }else if(log.startsWith("D")){
                    container.showPanel(new TextPanel(log, ColorPool.d_color));
                }else{
                    container.showPanel(new TextPanel(log, Color.GRAY));
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
