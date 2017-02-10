package com.forthe.xlog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;

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
            "调试","信息", "警告","错误","保存","Logcat"
        };
        extraItems = XLog.getExtraItems();
        padding = XLogUtils.dp2px(context, 12f);
        config = XLog.getConfig();
    }

    @Override
    public int getCount() {
        if(null == extraItems){
            return items.length;
        }else{
            return items.length + extraItems.size();
        }
    }

    @Override
    public String getItem(int position) {
        if(position < items.length){
            return items[position];
        }else{
            return extraItems.get(position - items.length);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
