package com.forthe.xlog.tools;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
        if(!(from instanceof Activity)){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        from.startActivity(intent);
    }

    public static void sendFileToQQ(Context from, String filePath){
        if(null == from || TextUtils.isEmpty(filePath)){
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if(!(from instanceof Activity)){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(Uri.fromFile(new File(filePath)), getMimeType(filePath));
        from.startActivity(intent);
    }


    public static void sendFile(Context from, String filePath){
        if(null == from || TextUtils.isEmpty(filePath)){
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("file/*");
        intent.putExtra(Intent.EXTRA_STREAM, "file://"+filePath);
        if(!(from instanceof Activity)){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        from.startActivity(intent);
    }


    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "text/plain";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }



    public static void viewUrl(Context context, String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            if(!(context instanceof Activity)){
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }
    }

}
