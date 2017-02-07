package com.forthe.xlog.frame;

import android.content.Context;
import android.content.res.Resources;

import com.forthe.xlog.R;

public class ColorPool {
    public static int d_color;
    public static int w_color;
    public static int e_color;
    public static int json_color;
    public static int url_color;
    private static boolean hasInited = false;
    public static void init(Context context){
        if(hasInited){
            return;
        }

        Resources resources = context.getResources();
        d_color = resources.getColor(R.color.xlog_d_color);
        w_color = resources.getColor(R.color.xlog_w_color);
        e_color = resources.getColor(R.color.xlog_e_color);
        json_color = resources.getColor(R.color.xlog_json_color);
        url_color = resources.getColor(R.color.xlog_url_color);
        hasInited = true;
    }
}
