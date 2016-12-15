package com.npsngs.debug;

import android.text.format.DateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class DEBugLogStorer {
    private File saveFile;
    private FileWriter fw;
    DEBugLogStorer(String dir) {
        String date = new StringBuilder(DateFormat.format("yyyy-MM-dd", System.currentTimeMillis())).toString();
        saveFile = new File(dir, String.format("log_%s.txt",date));
        if(!saveFile.exists()){
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                saveFile = null;
            }
        }
    }

    void saveLog(String log){
        if(saveFile == null){
            return;
        }

        if(fw == null){
            try {
                fw = new FileWriter(saveFile, true);
            }catch (IOException e) {
                e.printStackTrace();
                saveFile = null;
                fw = null;
                return;
            }
        }

        try {
            fw
            .append(DateFormat.format("[ HH:mm:ss ]", System.currentTimeMillis()))
            .append("\n")
            .append(log)
            .append("\n")
            .flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
