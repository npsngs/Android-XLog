package com.forthe.xlog;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

abstract class XLogConfig {
    private int flag =0x00;
    private SharedPreferences sp;
    XLogConfig(Context context) {
        sp = context.getSharedPreferences("ui_libs",Context.MODE_PRIVATE);
        flag = sp.getInt("debug_flags", 0);

    }

    boolean isDebugON(){
        return 0 != (flag&1);
    }

    boolean switchDebug(){
        if(isDebugON()){
            flag = flag&0xfffffffe;
        }else{
            flag = flag|0x00000001;
        }
        saveConfig();
        return isDebugON();
    }

    boolean isWarnON(){
        return 0 != (flag&2);
    }

    boolean switchWarn(){
        if(isWarnON()){
            flag = flag&0xfffffffd;
        }else{
            flag = flag|0x00000002;
        }
        saveConfig();
        return isWarnON();
    }

    boolean isErrorON(){
        return 0 != (flag&4);
    }

    boolean switchError(){
        if(isErrorON()){
            flag = flag&0xfffffffb;
        }else{
            flag = flag|0x00000004;
        }
        saveConfig();
        return isErrorON();
    }

    boolean isSaveON(){
        return 0 != (flag&8);
    }

    boolean switchSave(){
        if(isSaveON()){
            flag = flag&0xfffffff7;
        }else{
            flag = flag|0x00000008;
        }
        saveConfig();
        return isSaveON();
    }

    boolean isLogcatON(){
        return 0 != (flag&16);
    }

    boolean switchLogcat() {
        if (isLogcatON()) {
            flag = flag & 0xffffffef;
        } else {
            flag = flag | 0x00000010;
        }
        saveConfig();
        return isLogcatON();
    }

    boolean isInfoON(){
        return 0 != (flag&32);
    }

    boolean switchInfo() {
        if (isInfoON()) {
            flag = flag & 0xffffffdf;
        } else {
            flag = flag | 0x00000020;
        }
        saveConfig();
        return isLogcatON();
    }



    boolean isActivated(){
        return flag != 0x0;
    }

    private void saveConfig(){
        sp.edit().putInt("debug_flags", flag).apply();
    }

    protected abstract void onConfigChange();


    private List<String> extraItems;
    List<String> getExtraItems() {
        if(extraItems == null){
            extraItems = new ArrayList<>();
        }
        return extraItems;
    }

    void addSwitchItem(String key){
        if(extraItems == null){
            extraItems = new ArrayList<>();
        }

        if(extraItems.contains(key)){
            return;
        }

        extraItems.add(key);
    }

    boolean getSwitchItem(String key){
        if(null != extraItems && extraItems.contains(key)){
            return sp.getBoolean(key, false);
        }
        return false;
    }

    boolean switchExtraItem(String key){
        if(null != extraItems && extraItems.contains(key)){
            boolean item = sp.getBoolean(key, false);
            sp.edit().putBoolean(key, !item).apply();
            return !item;
        }
        return false;
    }

}
