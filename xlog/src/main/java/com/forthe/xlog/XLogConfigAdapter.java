package com.forthe.xlog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;


class XLogConfigAdapter extends BaseAdapter {
    private Context context = null;
    private String[] items;
    private int padding;
    XLogConfigAdapter(Context context) {
        this.context = context;
        items = new String[]{
            "调试信息","警告信息","错误信息","测试环境","保存信息","启用Logcat"
        };
        padding = XLogUtils.dp2px(context, 12f);
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
                    sw.setChecked(XLog.isDebugON());
                    break;
                case 1:
                    sw.setChecked(XLog.isWarnON());
                    break;
                case 2:
                    sw.setChecked(XLog.isErrorON());
                    break;
                case 3:
                    sw.setChecked(XLog.isTestON());
                    break;
                case 4:
                    sw.setChecked(XLog.isSaveON());
                    break;
                case 5:
                    sw.setChecked(XLog.isLogcatON());
                    break;
            }
        }


        @Override
        public void onClick(View v) {
            switch (position){
                case 0:
                    XLog.switchDebug();
                    break;
                case 1:
                    XLog.switchWarn();
                    break;
                case 2:
                    XLog.switchError();
                    break;
                case 3:
                    XLog.switchTest();
                    break;
                case 4:
                    XLog.switchSave();
                    break;
                case 5:
                    XLog.switchLogcat();
                    break;
            }
        }
    }
}
