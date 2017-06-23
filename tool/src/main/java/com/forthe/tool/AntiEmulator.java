package com.forthe.tool;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据设备的各种信息进行评分，分数扣光则判断为模拟器
 */
public class AntiEmulator {
    private String lastRateRecord;
    private List<Rater> raters;
    public AntiEmulator() {
        raters = new ArrayList<>();
        raters.add(new ThermalRater());
        raters.add(new DeviceInfoRater());
        raters.add(new PackageRater());
    }

    public boolean isOnEmulator(Context context){
        Score score = new Score(100);
        for(Rater rater:raters){
            rater.rate(context, score);
            if(score.isZero()){
                lastRateRecord = score.printRecord();
                return true;
            }
        }

        lastRateRecord = score.printRecord();
        return false;
    }

    public String getLastRateRecord() {
        return lastRateRecord;
    }

    private class Score{
        int score;
        Score(int score) {
            this.score = score;
        }

        List<RateRecord> records;

        boolean deduct(int deductScore, String reason){
            if(records == null){
                records = new ArrayList<>();
            }
            records.add(new RateRecord(deductScore, reason));
            score -= deductScore;
            return score <= 0;
        }

        boolean isZero(){
            return score <= 0;
        }

        String printRecord(){
            if(records == null || records.size() < 1){
                return "no reason\n";
            }

            StringBuilder sb = new StringBuilder();
            for (RateRecord record:records){
                sb.append(record.toString()).append("\n");
            }
            return sb.toString();
        }

    }

    private class RateRecord{
        int deductScore;
        String reason;
        RateRecord(int deductScore, String reason) {
            this.deductScore = deductScore;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return "RateRecord{" +
                    "deductScore=" + deductScore +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }

    interface Rater{
        void rate(Context context, Score score);
    }

    private class DeviceInfoRater implements Rater{

        @Override
        public void rate(Context context, Score score) {
            String brand = Build.BRAND.toLowerCase();
            String hardware = Build.HARDWARE.toLowerCase();
            String board = Build.BOARD.toLowerCase();
            String device = Build.DEVICE.toLowerCase();

            float density = context.getResources().getDisplayMetrics().density;
            boolean hasVibrator;
            try {
                Vibrator vibrator = (Vibrator) context.getSystemService(
                        Service.VIBRATOR_SERVICE);
                hasVibrator = vibrator.hasVibrator();
            } catch (Throwable e) {
                hasVibrator = false;
            }

            if(hardware.contains("vbox")){
                if(score.deduct(100, "Build.HARDWARE=="+hardware)){
                    return;
                }
            }

            if(device.contains("droid4x")){
                if(score.deduct(100, "Build.DEVICE=="+device)){
                    return;
                }
            }

            if(hardware.contains("vm")){
                if(score.deduct(80, "Build.HARDWARE=="+hardware)){
                    return;
                }
            }else if(hardware.contains("x86")){
                if(score.deduct(30, "Build.HARDWARE=="+hardware)){
                    return;
                }
            }


            if(brand.contains("generic") || brand.contains("android")) {
                if(score.deduct(30, "Build.BRAND=="+brand)){
                    return;
                }
            }


            if(board.contains("unknown")){
                if(score.deduct(15, "Build.BOARD=="+board)){
                    return;
                }
            }

            if(!hasVibrator){
                if(score.deduct(20, "has no vibrator")){
                    return;
                }
            }

            if(1f == density){
                if(score.deduct(5, "density == 1f")){
                    return;
                }
            }

            boolean isRoot = isRoot();
            if(isRoot){
                if(score.deduct(30, "is root device")){
                    return;
                }
            }

            try {
                String serial = Build.SERIAL.toLowerCase();
                if(serial.contains("unknown") || serial.contains("android")){
                    score.deduct(50, "Build.SERIAL == "+serial);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class ThermalRater implements Rater{
        @Override
        public void rate(Context context, Score score) {
            try {
                File f = new File("/sys/class/thermal");
                String[] files = f.list();
                if(files == null || files.length < 0){
                    score.deduct(65, "thermal has no file");
                    return;
                }
                boolean hasThermal = false;
                boolean hasCooling = false;
                for(String fname:files){
                    if(TextUtils.isEmpty(fname)){
                        continue;
                    }

                    if(fname.contains("thermal_zone")){
                        hasThermal = true;
                    }

                    if(fname.contains("cooling_device")){
                        hasCooling = true;
                    }

                    if(hasThermal && hasCooling){
                        return;
                    }
                }

                if(hasCooling){
                    score.deduct(100, "has cooling_device but not have thermal_zone");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 判断当前手机是否有ROOT权限
     */
    private boolean isRoot(){
        boolean bool = false;

        try{
            bool = !((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    class PackageRater implements Rater{

        @Override
        public void rate(Context context, Score score) {
            if(checkPackage(context, "de.robv.android.xposed.installer")){
                score.deduct(65, "installed xposed");
            }

            if(checkPackage(context, "com.virtualdroid.kit")){
                score.deduct(80, "installed zhuoshi");
            }
        }

        /**
         * 检测该包名所对应的应用是否存在
         * @param packageName
         * @return
         */
        public boolean checkPackage(Context context, String packageName)
        {
            if (packageName == null || "".equals(packageName))
                return false;
            try{
                context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
                return true;
            }catch (PackageManager.NameNotFoundException e){
                return false;
            }
        }
    }

}
