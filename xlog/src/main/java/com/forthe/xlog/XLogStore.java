package com.forthe.xlog;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.forthe.xlog.core.LogStore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class XLogStore implements LogStore{
    private boolean isNeedSaveToFile = false;
    private List<String> logs;
    private File saveFile;
    private FileWriter fw;
    private String saveDir;
    XLogStore(String dir, boolean isNeedSaveToFile) {
        logs = new ArrayList<>();
        this.saveDir = dir;
        setNeedSaveToFile(isNeedSaveToFile);
    }

    private void checkDir(String dir){
        File file = new File(dir);
        if(!file.exists() || !file.isDirectory()){
            file.mkdirs();
        }
    }

    private void checkFile(){
        if(saveFile != null || TextUtils.isEmpty(saveDir)){
            return;
        }
        checkDir(saveDir);

        String date = String.valueOf(DateFormat.format("yyyy-MM-dd", System.currentTimeMillis()));
        saveFile = new File(saveDir, String.format("log_%s.txt",date));
        if(!saveFile.exists()){
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                saveFile = null;
            }
        }
    }

    void setNeedSaveToFile(boolean needSaveToFile) {
        isNeedSaveToFile = needSaveToFile;
        if(isNeedSaveToFile){
            checkFile();
        }
    }

    private void saveLog(String log){
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
            .append(DateFormat.format("[HH:mm:ss]", System.currentTimeMillis()))
            .append("\n")
            .append(log)
            .append("\n")
            .flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeLog(String log) {
        logs.add(log);
        if(isNeedSaveToFile){
            saveLog(log);
        }
    }

    @Override
    public void clear() {
        logs.clear();
    }


    public List<String> getLogs() {
        return logs;
    }
}
