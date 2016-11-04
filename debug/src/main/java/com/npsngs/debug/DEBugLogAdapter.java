package com.npsngs.debug;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


class DEBugLogAdapter extends Adapter<String> implements DEBug.OnAddLogListener {
    private final int[] logColors = {0xffff2200,0xffe38204,0xff188b02};
    DEBugLogAdapter(Context mContext) {
        super(mContext);
        setData(DEBug.getLogs());
        DEBug.setDebugListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LogHolder logHolder;
        if(null == v){
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(12f);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setClickable(false);
            tv.setGravity(Gravity.START);
            int padding = DEBugUtils.dp2px(getContext(), 10);
            tv.setPadding(padding, padding, padding, padding);
            logHolder = new LogHolder(tv);
            tv.setTag(logHolder);
        }else{
            logHolder = (LogHolder) v.getTag();
        }

        logHolder.bind(position);
        return logHolder.tv;
    }

    class LogHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView tv;
        int position;
        public LogHolder(TextView tv) {
            this.tv = tv;
            tv.setOnClickListener(this);
            tv.setOnLongClickListener(this);
        }

        void bind(int position){
            this.position = position;

            String log = getItem(position);
            if(log.startsWith("E")){
                tv.setTextColor(logColors[0]);
            }else if(log.startsWith("W")){
                tv.setTextColor(logColors[1]);
            }else if(log.startsWith("D")){
                tv.setTextColor(logColors[2]);
            }

            if(!TextUtils.isEmpty(log) && log.length() > 200){
                log = log.substring(0, 180);
                log = log+"...";
            }
            tv.setText(log);
        }

        @Override
        public void onClick(View v) {
            if(null != onShowParseText){
                String log = getItem(position);
                if(log.startsWith("E")){
                    onShowParseText.showParsedText(log, logColors[0]);
                }else if(log.startsWith("W")){
                    onShowParseText.showParsedText(log, logColors[1]);
                }else if(log.startsWith("D")){
                    onShowParseText.showParsedText(log, logColors[2]);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            String text = getItem(position);
            DEBugUtils.sendText(getContext(), text);
            return true;
        }
    }


    @Override
    public void onLogAdded(String log) {
        addData(log);
    }

    @Override
    public void onLogClear() {
        clear();
    }

    private DEBugPopup.OnShowParseText onShowParseText;
    public void setOnShowParseText(DEBugPopup.OnShowParseText onShowParseText) {
        this.onShowParseText = onShowParseText;
    }
}
