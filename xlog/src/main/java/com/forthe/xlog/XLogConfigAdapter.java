package com.forthe.xlog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.forthe.xlog.tools.XLogUtils;

import java.util.List;


class XLogConfigAdapter extends BaseAdapter {
    private Context context = null;
    private String[] items;
    private int padding;
    private XLogConfig config;
    private List<String> extraItems;
    XLogConfigAdapter(Context context) {
        this.context = context;
        items = new String[]{
            "Debug","Info", "Warn","Error","Save","Logcat"
        };
        extraItems = XLog.getExtraItems();
        padding = XLogUtils.dp2px(context, 12f);
        config = XLog.getConfig();
    }

    @Override
    public int getCount() {
        if(null == extraItems){
            return items.length+1;
        }else{
            return items.length + extraItems.size()+1;
        }
    }

    @Override
    public String getItem(int position) {
        if(position < items.length){
            return items[position];
        }else if(position >= items.length && position < items.length + extraItems.size()){
            return extraItems.get(position - items.length);
        }else{
            return XLog.getExtraInfo(context);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount()-1?1:0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if(type == 0){
            View v = convertView;
            Holder holder;
            if(v == null){
                Switch sw = new Switch(context);
                sw.setBackgroundColor(0xffe3e3e3);
                sw.setTextSize(15.0f);
                sw.setTextColor(0xff333333);
                sw.setPadding(padding,padding,padding,padding);
                holder = new Holder(sw);
                v = sw;
                v.setTag(holder);
            }else{
                holder = (Holder) v.getTag();
            }
            holder.bindPosition(position);
            return v;
        }else{
            TextView tv;
            if(convertView == null){
                tv = new TextView(context);
                tv.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setTextSize(14f);
                tv.setTextIsSelectable(true);
                tv.setGravity(Gravity.LEFT);
                int padding = XLogUtils.dp2px(context, 10);
                tv.setPadding(padding, padding, padding, padding);
                tv.setTextColor(0xff787878);
                tv.setSingleLine(false);
            }else {
                tv = (TextView) convertView;
            }
            tv.setText(getItem(position));
            return tv;
        }
    }



    private class Holder implements View.OnClickListener {
        Switch sw;
        int position;
        Holder(Switch sw) {
            this.sw = sw;
            sw.setOnClickListener(this);
        }

        void bindPosition(int position){
            this.position = position;
            sw.setText(getItem(position));

            switch (position){
                case 0:
                    sw.setChecked(config.isDebugON());
                    break;
                case 1:
                    sw.setChecked(config.isInfoON());
                    break;
                case 2:
                    sw.setChecked(config.isWarnON());
                    break;
                case 3:
                    sw.setChecked(config.isErrorON());
                    break;
                case 4:
                    sw.setChecked(config.isSaveON());
                    break;
                case 5:
                    sw.setChecked(config.isLogcatON());
                    break;
                default:
                    if(position >= items.length){
                        sw.setChecked(config.getSwitchItem(extraItems.get(position-items.length)));
                    }
                    break;
            }
        }


        @Override
        public void onClick(View v) {
            switch (position){
                case 0:
                    config.switchDebug();
                    break;
                case 1:
                    config.switchInfo();
                    break;
                case 2:
                    config.switchWarn();
                    break;
                case 3:
                    config.switchError();
                    break;
                case 4:
                    config.switchSave();
                    break;
                case 5:
                    config.switchLogcat();
                    break;
                default:
                    if(position >= items.length){
                        config.switchExtraItem(extraItems.get(position-items.length));
                    }
                    break;
            }
        }
    }
}
