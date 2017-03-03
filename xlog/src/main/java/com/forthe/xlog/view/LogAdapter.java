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
import com.forthe.xlog.panel.filters.PatternFilter;
import com.forthe.xlog.panel.filters.TagFilter;
import com.forthe.xlog.tools.XLogUtils;

import java.util.regex.Pattern;


public abstract class LogAdapter extends FilterAdapter<String>{
    protected Container container;
    private TagFilter tagFilter;
    private PatternFilter patternFilter;
    public LogAdapter(Context mContext, Container container) {
        super(mContext);
        this.container = container;
    }

    protected abstract boolean onFilterTag(String tag, String item);
    public void addFilterTag(String tag){
        if(tagFilter == null){
            tagFilter = new TagFilter() {
                @Override
                protected boolean onFilter(String tag, String item) {
                    return onFilterTag(tag, item);
                }
            };
            tagFilter.addTag(tag);
            addItemFilter(tagFilter);
        }else{
            tagFilter.addTag(tag);
            onFilterChange();
        }
    }

    public void removeFilterTag(String tag){
        if(tagFilter != null){
            tagFilter.removeTag(tag);
            onFilterChange();
        }
    }

    public void setFilterPattern(Pattern pattern){
        if(patternFilter == null){
            patternFilter = new PatternFilter();
            patternFilter.setFindPattern(pattern);
            addItemFilter(patternFilter);
        }else{
            patternFilter.setFindPattern(pattern);
            onFilterChange();
        }
    }

    public void removeFilterPattern(){
        if(patternFilter != null){
            patternFilter.setFindPattern(null);
            removeItemFilter(patternFilter);
            patternFilter = null;
        }
    }

    public LogAdapter(Context mContext) {
        super(mContext);
    }

    public void setContainer(Container container) {
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
            String typeTag = getTypeTag(log);
            tv.setTextColor(getColorByType(typeTag));

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
                String typeTag = getTypeTag(log);
                container.showPanel(new TextPanel(log, getColorByType(typeTag)));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            String text = getItem(position);
            XLogUtils.sendText(getContext(), text);
            return true;
        }
    }

    private int getColorByType(String typeTag){
        switch (typeTag){
            case "E":
                return ColorPool.e_color;
            case "I":
                return ColorPool.i_color;
            case "D":
                return ColorPool.d_color;
            case "W":
                return ColorPool.w_color;
            default:
                return Color.GRAY;
        }
    }


    protected abstract String getTypeTag(String log);


}
