package com.forthe.xlog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Vector;

public class XLog {
    private static int flag =0x00;
    private static SharedPreferences sp;
    private static final Vector<String> logs = new Vector<>();
    private static String logSaveDir = null;
    private static String crashSaveDir = null;
    private static XLogReceiver logReceiver = null;
    private static XLogNotifier logNotifier = null;
    private static XLogStorer logStorer = null;

    public static void init(Context context){
        init(context,null);
    }

    public static void init(Context context, String saveDir){
        sp = context.getSharedPreferences("ui_libs",Context.MODE_PRIVATE);
        flag = sp.getInt("debug_flags", 0);
        logReceiver = new XLogReceiver() {
            @Override
            void onReceiveLog(String log) {
                onAddLog(log);
            }
        };
        if(isErrorON() || isWarnON() || isDebugON()){
            logReceiver.init();
        }

        if(!TextUtils.isEmpty(saveDir)){
            File pathFile = new File(saveDir);
            if(!pathFile.exists()){
                pathFile.mkdirs();
            }

            File crashSaveFile = new File(pathFile, "crash");
            if(!crashSaveFile.exists()){
                crashSaveFile.mkdir();
            }
            crashSaveDir = crashSaveFile.getPath();
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(Thread
                    .getDefaultUncaughtExceptionHandler(), crashSaveDir));


            File logSaveFile = new File(pathFile, "log");
            if(!logSaveFile.exists()){
                logSaveFile.mkdir();
            }
            logSaveDir = logSaveFile.getPath();

            if(isSaveON()){
                logStorer = new XLogStorer(logSaveDir);
            }
        }
    }

    static String getCrashSaveDir() {
        return crashSaveDir;
    }

    static String getLogSaveDir() {
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
        w("", log);
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
        e("", log);
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

        logReceiver.receiveLog(log);
    }

    private static void onAddLog(String log){
        logs.add(log);
        if(isSaveON()){
            logStorer.saveLog(log);
        }

        if(null != logNotifier){
            logNotifier.onNotifyLogAdd(log);
        }
    }

    public static void clearLog(){
        logs.clear();
        if(null != logNotifier){
            logNotifier.onNotifyLogClear();
        }
    }

    static Vector<String> getLogs() {
        return logs;
    }


    static boolean isDebugON(){
        return 0 != (flag&1);
    }

    static boolean switchDebug(){
        if(isDebugON()){
            flag = flag&0xfffffffe;
        }else{
            flag = flag|0x00000001;
            logReceiver.init();
        }
        saveConfig();
        return isDebugON();
    }

    static boolean isWarnON(){
        return 0 != (flag&2);
    }

    static boolean switchWarn(){
        if(isWarnON()){
            flag = flag&0xfffffffd;
        }else{
            flag = flag|0x00000002;
            logReceiver.init();
        }
        saveConfig();
        return isWarnON();
    }

    static boolean isErrorON(){
        return 0 != (flag&4);
    }

    static boolean switchError(){
        if(isErrorON()){
            flag = flag&0xfffffffb;
        }else{
            flag = flag|0x00000004;
            logReceiver.init();
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


    static boolean isLogcatON(){
        return 0 != (flag&16);
    }

    static boolean switchLogcat(){
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
            if(null == logStorer){
                logStorer = new XLogStorer(logSaveDir);
            }
        }
        saveConfig();
        return isSaveON();
    }


    private static void saveConfig(){
        sp.edit().putInt("debug_flags", flag).apply();
    }


    static void setOnLogChangeListener(XLog.OnLogChangeListener onLogChangeListener) {
        if(null == onLogChangeListener){
            return;
        }

        if(null == logNotifier){
            logNotifier = new XLogNotifier();
        }
        logNotifier.setOnLogChangeListener(onLogChangeListener);
    }

    interface OnLogChangeListener{
        void onLogAdded(String log);
        void onLogClear();
    }

    private static XLogPopup buGPopupWindow = null;

    public static final int PAGE_CONFIG = 0;
    public static final int PAGE_LOGS = 1;
    public static final int PAGE_CRASH = 2;
    public static void show(Activity activity, int page){
        if(buGPopupWindow == null){
            buGPopupWindow = new XLogPopup(activity);
        }else {
            if(!buGPopupWindow.isCurrentActivity(activity)){
                buGPopupWindow.dismiss();
                buGPopupWindow = new XLogPopup(activity);
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
