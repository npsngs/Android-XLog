package com.npsngs.debug;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.Vector;

public class DEBug {
    private static int flag =0x00;
    private static SharedPreferences sp;
    private static final Vector<String> logs = new Vector<>();

    public static void init(Context context){
        sp = context.getSharedPreferences("ui_libs",Context.MODE_PRIVATE);
        flag = sp.getInt("debug_flags", 0);
    }

    private static String logSaveDir;
    public static void initCrashDebug(String logSaveDir){
        DEBug.logSaveDir = logSaveDir;
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(Thread
                .getDefaultUncaughtExceptionHandler(), logSaveDir));
    }

    public static String getLogSaveDir() {
        return logSaveDir;
    }

    public static boolean isDEBugActive(){
        return flag != 0;
    }


    public static void d(String log){
        d("", log);
    }

    public static void d(String tag, String log){
        if(isDebugON()){
            addLog("D",tag,log);
            if(isLogcatON()){
                Log.d(tag,log);
            }
        }
    }

    public static void w(String log){
        d("", log);
    }

    public static void w(String tag, String log){
        if(isWarnON()){
            addLog("W",tag,log);
            if(isLogcatON()){
                Log.w(tag,log);
            }
        }
    }

    public static void e(String log){
        d("", log);
    }

    public static void e(String tag, String log){
        if(isErrorON()){
            addLog("E",tag,log);
            if(isLogcatON()){
                Log.e(tag,log);
            }
        }
    }


    private static void addLog(String lvl, String tag, String msg){
        String log;
        if(TextUtils.isEmpty(tag)){
            log = String.format("%s\t\t%s\t%s", lvl,tag,msg);
        }else{
            log = String.format("%s\t\t%s", lvl,msg);
        }
        logs.add(log);

        if(null != onAddLogListener){
            onAddLogListener.onLogAdded(log);
        }
    }

    public static void clearLog(){
        logs.clear();
        if(null != onAddLogListener){
            onAddLogListener.onLogClear();
        }
    }

    static Vector<String> getLogs() {
        return logs;
    }


    public static boolean isDebugON(){
        return 0 != (flag&1);
    }

    public static boolean switchDebug(){
        if(isDebugON()){
            flag = flag&0xfffffffe;
        }else{
            flag = flag|0x00000001;
        }
        saveConfig();
        return isDebugON();
    }

    public static boolean isWarnON(){
        return 0 != (flag&2);
    }

    public static boolean switchWarn(){
        if(isWarnON()){
            flag = flag&0xfffffffd;
        }else{
            flag = flag|0x00000002;
        }
        saveConfig();
        return isWarnON();
    }

    public static boolean isErrorON(){
        return 0 != (flag&4);
    }

    public static boolean switchError(){
        if(isErrorON()){
            flag = flag&0xfffffffb;
        }else{
            flag = flag|0x00000004;
        }
        saveConfig();
        return isErrorON();
    }

    public static boolean isTestON(){
        return 0 != (flag&8);
    }

    public static boolean switchTest(){
        if(isTestON()){
            flag = flag&0xfffffff7;
        }else{
            flag = flag|0x00000008;
        }
        saveConfig();
        return isTestON();
    }


    public static boolean isLogcatON(){
        return 0 != (flag&16);
    }

    public static boolean switchLogcat(){
        if(isLogcatON()){
            flag = flag&0xffffffef;
        }else{
            flag = flag|0x00000010;
        }
        saveConfig();
        return isLogcatON();
    }

    public static boolean isSaveON(){
        return 0 != (flag&32);
    }

    public static boolean switchSave(){
        if(isSaveON()){
            flag = flag&0xffffffdf;
        }else{
            flag = flag|0x00000020;
        }
        saveConfig();
        return isSaveON();
    }


    private static void saveConfig(){
        sp.edit().putInt("debug_flags", flag).apply();
    }



    private static OnAddLogListener onAddLogListener = null;
    static void setDebugListener(OnAddLogListener debugListener) {
        DEBug.onAddLogListener = debugListener;
    }
    interface OnAddLogListener{
        void onLogAdded(String log);
        void onLogClear();
    }

    private static DEBugPopup buGPopupWindow = null;

    public static final int PAGE_CONFIG = 0;
    public static final int PAGE_LOGS = 1;
    public static final int PAGE_CRASH = 2;
    public static void show(Activity activity, int page){
        if(buGPopupWindow == null){
            buGPopupWindow = new DEBugPopup(activity);
        }else {
            if(!buGPopupWindow.isCurrentActivity(activity)){
                buGPopupWindow.dismiss();
                buGPopupWindow = new DEBugPopup(activity);
            }
        }
        buGPopupWindow.show(page);
    }

    public static void show(Activity activity){
        show(activity, PAGE_CONFIG);
    }

    public static void dismiss(Activity activity){
        if(buGPopupWindow != null && buGPopupWindow.isCurrentActivity(activity)){
            buGPopupWindow.dismiss();
        }
    }
}
