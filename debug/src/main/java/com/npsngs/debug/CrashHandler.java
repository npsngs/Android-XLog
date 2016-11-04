
package com.npsngs.debug;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;

class CrashHandler implements UncaughtExceptionHandler {
    private final UncaughtExceptionHandler defaultHandler;
    private String saveToDir;
    public CrashHandler(UncaughtExceptionHandler defaultHandler, String saveToDir) {
        this.defaultHandler = defaultHandler;
        this.saveToDir = saveToDir;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                String errTime = String.format("%4d%02d%02d_%02d%02d%02d",
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH)+1,
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.SECOND));
                File errLog = new File(saveToDir + "/errlog" + errTime + ".txt");
                if (!errLog.exists()) {
                    errLog.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(errLog, false);
                ex.printStackTrace(new PrintStream(fos));
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultHandler.uncaughtException(thread, ex);
    }
}
