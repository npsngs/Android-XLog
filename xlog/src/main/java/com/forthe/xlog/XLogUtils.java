package com.forthe.xlog;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class XLogUtils {
    public static int dp2px(Context context, float dp){
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }


    public static void sendText(Context from, String text){
        if(null == from || TextUtils.isEmpty(text)){
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        from.startActivity(intent);
    }

    public static void sendFile(Context from, String filePath){
        if(null == from || TextUtils.isEmpty(filePath)){
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("file/*");
        intent.putExtra(Intent.EXTRA_STREAM, "file://"+filePath);
        from.startActivity(intent);
    }
}
