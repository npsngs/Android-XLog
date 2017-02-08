package com.forthe.xlog.tools;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

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


    public static void viewUrl(Context context, String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }
    }

}
