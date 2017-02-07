package com.forthe.xlog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;

import com.forthe.xlog.tools.XLogUtils;


class XLogConfigAdapter extends BaseAdapter {
    private Context context = null;
    private String[] items;
    private int padding;
    private XLogConfig config;
    XLogConfigAdapter(Context context) {
        this.context = context;
        items = new String[]{
            "调试信息","警告信息","错误信息","保存信息","启用Logcat"
        };
        padding = XLogUtils.dp2px(context, 12f);
        config = XLog.getConfig();
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int position) {
        return items[position];
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
                    sw.setChecked(config.isWarnON());
                    break;
                case 2:
                    sw.setChecked(config.isErrorON());
                    break;
                case 3:
                    sw.setChecked(config.isSaveON());
                    break;
                case 4:
                    sw.setChecked(config.isLogcatON());
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
                    config.switchWarn();
                    break;
                case 2:
                    config.switchError();
                    break;
                case 3:
                    config.switchSave();
                    break;
                case 4:
                    config.switchLogcat();
                    break;
            }
        }
    }
}
