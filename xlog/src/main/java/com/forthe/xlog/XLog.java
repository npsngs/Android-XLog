package com.forthe.xlog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.forthe.xlog.core.LogNotifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * ▷ ▽ △ ◁ ♂ ♀
 */
public class XLog {
    private static String logSaveDir;
    private static String crashSaveDir;
    private static XLogReceiver logReceiver;
    private static LogNotifier logNotifier;
    private static XLogStore logStore;
    private static XLogConfig config;
    private static boolean hasInit = false;
    public static boolean isActivated(){
        if(null != config){
            return config.isActivated();
        }
        return false;
    }

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
        if(config.isDebugON() && !TextUtils.isEmpty(log)){
            addLog("D",tag,log);
            if(config.isLogcatON()){
                Log.d(tag,log);
            }
        }
    }

    public static void i(String log){
        i("", log);
    }

    public static void i(String tag, String log){
        if(config.isDebugON() && !TextUtils.isEmpty(log)){
            addLog("I",tag,log);
            if(config.isLogcatON()){
                Log.d(tag,log);
            }
        }
    }

    public static void e(Throwable t){
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String log = sw.toString();
        e(log);
    }

    public static void w(Throwable t){
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String log = sw.toString();
        w(log);
    }

    public static void w(String log){
        w("", log);
    }

    public static void w(String tag, String log){
        if(config.isWarnON() && !TextUtils.isEmpty(log)){
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
        if(config.isErrorON() && !TextUtils.isEmpty(log)){
            addLog("E",tag,log);
            if(config.isLogcatON()){
                Log.e(tag,log);
            }
        }
    }


    private static void addLog(String lvl, String tag, String msg){
        String log;
        if(!TextUtils.isEmpty(tag)){
            log = String.format("%s\t%s\t%s", lvl,tag,msg);
        }else{
            log = String.format("%s\t%s", lvl,msg);
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
        List<String> logs = logStore.getLogs();
        List<String> ret = new ArrayList<>(logs.size());
        ret.addAll(logs);
        return ret;
    }

    static void setLogNotifier(LogNotifier logNotifier) {
        XLog.logNotifier = logNotifier;
    }


    static final int PAGE_CONFIG = 0;
    static final int PAGE_LOGS = 1;
    static final int PAGE_CRASH = 2;
    static void show(Activity activity, int page){
        new XLogWindow(activity).show(page);
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

    static XLogConfig getConfig() {
        return config;
    }


    static void onClearRef(){
        setLogNotifier(null);
    }



    public static List<String> getExtraItems() {
        if(null != config){
            return config.getExtraItems();
        }
        return null;
    }

    public static void addSwitchItem(String key){
        if(null != config){
            config.addSwitchItem(key);
        }
    }

    public static boolean getSwitchItem(String key){
        if(null != config){
            return config.getSwitchItem(key);
        }
        return false;
    }


    private static String extraInfo;
    public static String getExtraInfo(Context context) {
        if(TextUtils.isEmpty(extraInfo)){
            StringBuilder sb = new StringBuilder();
            sb.append("------------------------------------\n");
            sb.append("[Sdk]:\t").append(Build.VERSION.SDK_INT).append("\n");
            sb.append("[Device]:\t").append(Build.DEVICE).append("\n");
            sb.append("[Package]:\t").append(context.getApplicationInfo().packageName).append("\n");
            sb.append("------------------------------------\n");
            extraInfo = sb.toString();
        }
        return extraInfo;
    }
    public static void setExtraInfo(String extraInfo) {
        XLog.extraInfo = extraInfo;
    }
}
