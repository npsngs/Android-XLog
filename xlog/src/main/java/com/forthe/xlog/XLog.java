package com.forthe.xlog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.forthe.xlog.core.LogNotifier;

import java.io.File;
import java.util.List;

public class XLog {
    private static String logSaveDir;
    private static String crashSaveDir;
    private static XLogReceiver logReceiver;
    private static LogNotifier logNotifier;
    private static XLogStore logStore;
    private static XLogConfig config;
    private static boolean hasInit = false;
    public static void init(Context context){
        init(context,null);
    }

    public static void init(Context context, String saveDir){
        if(hasInit){
            return;
        }
        config = new XLogConfig(context) {
            @Override
            protected void onConfigChange() {
                if(config.isActivated()){
                    logReceiver.init();
                    if(config.isSaveON()){
                        logStore.setNeedSaveToFile(true);
                    }
                }
            }
        };

        if(!TextUtils.isEmpty(saveDir)){
            File pathFile = new File(saveDir);
            File crashSaveFile = new File(pathFile, "crash");
            File logSaveFile = new File(pathFile, "log");

            crashSaveDir = crashSaveFile.getPath();
            logSaveDir = logSaveFile.getPath();

            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(Thread
                    .getDefaultUncaughtExceptionHandler(), crashSaveDir));
        }


        logStore = new XLogStore(logSaveDir, config.isSaveON());
        logReceiver = new XLogReceiver() {

            @Override
            protected void onReceiveLog(String log) {
                logStore.storeLog(log);
                if(null != logNotifier){
                    logNotifier.onLogAdd(log);
                }
            }
        };

        if(config.isActivated()){
            logReceiver.init();
        }
        hasInit = true;
    }

    static String getCrashSaveDir() {
        return crashSaveDir;
    }

    static String getLogSaveDir() {
        return logSaveDir;
    }



    public static void d(String log){
        d("", log);
    }

    public static void d(String tag, String log){
        if(config.isDebugON()){
            addLog("D",tag,log);
            if(config.isLogcatON()){
                Log.d(tag,log);
            }
        }
    }

    public static void w(String log){
        w("", log);
    }

    public static void w(String tag, String log){
        if(config.isWarnON()){
            addLog("W",tag,log);
            if(config.isLogcatON()){
                Log.w(tag,log);
            }
        }
    }

    public static void e(String log){
        e("", log);
    }

    public static void e(String tag, String log){
        if(config.isErrorON()){
            addLog("E",tag,log);
            if(config.isLogcatON()){
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



    static void clearLog(){
        logStore.clear();
        if(null != logNotifier){
            logNotifier.onLogClear();
        }
    }

    static List<String> getLogs() {
        return logStore.getLogs();
    }

    static void setLogNotifier(LogNotifier logNotifier) {
        XLog.logNotifier = logNotifier;
    }

    private static XLogWindow buGPopupWindow = null;

    static final int PAGE_CONFIG = 0;
    static final int PAGE_LOGS = 1;
    static final int PAGE_CRASH = 2;
    static void show(Activity activity, int page){
        if(buGPopupWindow == null){
            buGPopupWindow = new XLogWindow(activity);
        }else {
            if(!buGPopupWindow.isCurrentActivity(activity)){
                buGPopupWindow.dismiss();
                buGPopupWindow = new XLogWindow(activity);
            }
        }
        buGPopupWindow.show(page);
    }

    public static void show(Activity activity){
        show(activity, PAGE_CONFIG);
    }

    public static void showLog(Activity activity){
        show(activity, PAGE_LOGS);
    }

    public static void showCrash(Activity activity){
        show(activity, PAGE_CRASH);
    }

    public static void dismiss(Activity activity){
        if(buGPopupWindow != null && buGPopupWindow.isCurrentActivity(activity)){
            buGPopupWindow.dismiss();
        }
    }

    static XLogConfig getConfig() {
        return config;
    }
}
