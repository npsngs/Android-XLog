package com.npsngs.debug;

import android.text.format.DateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

class CrashHandler implements UncaughtExceptionHandler {
    private final UncaughtExceptionHandler defaultHandler;
    private String saveToDir;
    CrashHandler(UncaughtExceptionHandler defaultHandler, String saveToDir) {
        this.defaultHandler = defaultHandler;
        this.saveToDir = saveToDir;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        try {
            CharSequence date = DateFormat.format("yyyy-MM-dd_HHmmss", System.currentTimeMillis());
            File errLog = new File(saveToDir, String.format("crash_%s.txt", date));
            if (!errLog.exists()) {
                boolean ret = errLog.createNewFile();
                if(!ret){
                    return;
                }
            }
            FileOutputStream fos = new FileOutputStream(errLog, false);
            ex.printStackTrace(new PrintStream(fos));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultHandler.uncaughtException(thread, ex);
    }
}
