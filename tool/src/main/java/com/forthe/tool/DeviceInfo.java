package com.forthe.tool;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;

public class DeviceInfo {

    public static String showBuild(Context context) {
        try {
            StringBuilder builder = new StringBuilder();
            //noinspection deprecation,deprecation
            builder
                    .append("\nBuild.BOARD\t:").append(Build.BOARD)
                    .append("\nBuild.BOOTLOADER\t:").append(Build.BOOTLOADER)
                    .append("\nBuild.BRAND\t:").append(Build.BRAND)
                    .append("\nBuild.CPU_ABI\t:").append(Build.CPU_ABI)
                    .append("\nBuild.CPU_ABI2\t:").append(Build.CPU_ABI2)
                    .append("\nBuild.DEVICE\t:").append(Build.DEVICE)
                    .append("\nBuild.DISPLAY\t:").append(Build.DISPLAY)
                    .append("\nBuild.FINGERPRINT\t:").append(Build.FINGERPRINT)
                    .append("\nBuild.HARDWARE\t:").append(Build.HARDWARE)
                    .append("\nBuild.HOST\t:").append(Build.HOST)
                    .append("\nBuild.ID\t:").append(Build.ID)
                    .append("\nBuild.MANUFACTURER\t:").append(Build.MANUFACTURER)
                    .append("\nBuild.MODEL\t:").append(Build.MODEL)
                    .append("\nBuild.PRODUCT\t:").append(Build.PRODUCT)
                    .append("\nBuild.SERIAL\t:").append(Build.SERIAL)
                    .append("\nBuild.TAGS\t:").append(Build.TAGS)
                    .append("\nBuild.TIME\t:").append(Build.TIME)
                    .append("\nBuild.TYPE\t:").append(Build.TYPE)
                    .append("\nBuild.UNKNOWN\t:").append(Build.UNKNOWN)
                    .append("\nBuild.USER\t:").append(Build.USER)
                    .append("\nIS_ROOT\t:").append(isRoot());

            float density = context.getResources().getDisplayMetrics().density;
            builder.append("\nDensity\t:").append(density);


            boolean hasVibrator;
            try {
                Vibrator vibrator = (Vibrator) context.getSystemService(
                        Service.VIBRATOR_SERVICE);
                hasVibrator = vibrator.hasVibrator();
            } catch (Throwable e) {
                hasVibrator = false;
            }

            builder.append("\nHasVibrator\t:").append(hasVibrator);

            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            builder.append("\nAndroid_id\t:").append(android_id);

            String mccmcn = getMobileCode(context);
            builder.append("\nMccMnc\t:").append(mccmcn);

            String cpuinfo = getCPUInfo();
            builder.append("\nCpu\t:").append(cpuinfo);

            String hardwareAddr = getHardwareAddress();
            builder.append("\nHardwareAddress\t:").append(hardwareAddr);

            String macFromFile = getHardwareAddress();
            builder.append("\nMacFromFile\t:").append(macFromFile);


            String netop = getNetworkOperator(context);
            builder.append("\nNetworkOperator\t:").append(netop);

            String netopn = getNetworkOperatorName(context);
            builder.append("\nNetworkOperatorName\t:").append(netopn);

            String wifiMac = getWifiMacAddress(context);
            builder.append("\nWifiMac\t:").append(wifiMac);


            String phonyDeviceID = getTelephonyDeviceID(context);
            builder.append("\nPhonyDeviceID\t:").append(phonyDeviceID);

            try {
                TelephonyManager telephonyManager =
                        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String phonenum1 = telephonyManager.getLine1Number();
                builder.append("\nPhonenum1\t:").append(phonenum1);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                File f = new File("/sys/class/thermal");
                builder.append("\nThermal\t:").append(Arrays.asList(f.list()));
            }catch (Exception e){
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                builder.append("\nThermal\t:").append(stringWriter.toString());
            }

            String diskstats = getDiskstats();
            builder.append("\nDiskstats\t:").append(diskstats);
            return builder.toString();
        }catch (Exception e){
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }


    /**
     * 判断当前手机是否有ROOT权限
     * @return
     */
    public static boolean isRoot(){
        boolean bool = false;

        try{
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {

        }
        return bool;
    }

    public static boolean hasPermission(Context context, String permissionName) {
        boolean hasPermission = false;
        if(Build.VERSION.SDK_INT >= 23) {
            try {
                Class contextClass = Class.forName("android.content.Context");
                Method checkSelfPermission = contextClass.getMethod("checkSelfPermission", new Class[]{String.class});
                int var5 = ((Integer)checkSelfPermission.invoke(context, new Object[]{permissionName})).intValue();
                if(var5 == 0) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } catch (Throwable t) {
                hasPermission = false;
            }
        } else {
            PackageManager packageManager = context.getPackageManager();
            if(packageManager.checkPermission(permissionName, context.getPackageName()) == 0) {
                hasPermission = true;
            }
        }

        return hasPermission;
    }


    public static String getSubscribeID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String subscriberID = null;
        if(hasPermission(context, "android.permission.READ_PHONE_STATE")) {
            subscriberID = telephonyManager.getSubscriberId();
        }

        return subscriberID;
    }
    public static String getMobileCode(Context context) {
        if(getSubscribeID(context) == null) {
            return null;
        } else {
            int mcc = context.getResources().getConfiguration().mcc;
            int mnc = context.getResources().getConfiguration().mnc;
            if(mcc != 0) {
                String var3 = String.valueOf(mnc);
                if(mnc < 10) {
                    var3 = String.format("%02d", new Object[]{Integer.valueOf(mnc)});
                }

                return mcc + var3;
            } else {
                return null;
            }
        }
    }


    private static String getHardwareAddress() {
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

            NetworkInterface networkInterface;
            do {
                if(!networkInterfaces.hasMoreElements()) {
                    return null;
                }

                networkInterface = (NetworkInterface)networkInterfaces.nextElement();
            } while(!"wlan0".equals(networkInterface.getName()) && !"eth0".equals(networkInterface.getName()));

            byte[] hardwareAddress = networkInterface.getHardwareAddress();
            if(hardwareAddress != null && hardwareAddress.length != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                byte[] bytes = hardwareAddress;
                int length = hardwareAddress.length;

                for(int i = 0; i < length; ++i) {
                    byte b = bytes[i];
                    stringBuilder.append(String.format("%02X:", new Object[]{Byte.valueOf(b)}));
                }

                if(stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }

                return stringBuilder.toString().toLowerCase(Locale.getDefault());
            } else {
                return null;
            }
        } catch (Throwable t) {
            return null;
        }
    }

    private static String getMacFromFiles() {
        try {
            String[] addressFileNames = new String[]{"/sys/class/net/wlan0/address", "/sys/class/net/eth0/address", "/sys/devices/virtual/net/wlan0/address"};

            for(int i = 0; i < addressFileNames.length; ++i) {
                try {
                    String mac = readFile(addressFileNames[i]);
                    if(mac != null) {
                        return mac;
                    }
                } catch (Throwable t1) {
                }
            }
        } catch (Throwable t2) {
        }
        return null;
    }


    private static String readFile(String fileName) {
        String fileContent = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = null;
            if(fileReader != null) {
                try {
                    bufferedReader = new BufferedReader(fileReader, 1024);
                    fileContent = bufferedReader.readLine();
                } finally {
                    if(fileReader != null) {
                        try {
                            fileReader.close();
                        } catch (Throwable throwable) {
                        }
                    }

                    if(bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable t1) {
                        }
                    }

                }
            }
        } catch (Throwable t3) {
        }

        return fileContent;
    }


    public static String getCPUInfo() {
        String lineStr = null;
        FileReader fileReader;
        BufferedReader bufferedReader;

        StringBuilder sb = new StringBuilder("\n");
        try {
            fileReader = new FileReader("/proc/cpuinfo");
            if(fileReader != null) {
                try {
                    bufferedReader = new BufferedReader(fileReader, 1024);
                    do{
                        lineStr = bufferedReader.readLine();
                        if(lineStr != null) {
                            sb.append(lineStr).append("\n");
                        }
                    }while (lineStr != null);

                    bufferedReader.close();
                    fileReader.close();
                } catch (Throwable throwable) {
                    Log.e("getCPUInfo", "Could not readByteBuffer from file /proc/cpuinfo", throwable);
                }
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            Log.e("getCPUInfo", "Could not open file /proc/cpuinfo", e);
        }

        return "";
    }


    public static String getDiskstats() {
        String lineStr;
        FileReader fileReader;
        BufferedReader bufferedReader;

        StringBuilder sb = new StringBuilder("\n");
        try {
            fileReader = new FileReader("/proc/diskstats");
            if(fileReader != null) {
                try {
                    bufferedReader = new BufferedReader(fileReader, 1024);
                    do{
                        lineStr = bufferedReader.readLine();
                        if(lineStr != null){
                            sb.append(lineStr).append("\n");
                        }
                    }while (lineStr != null);

                    bufferedReader.close();
                    fileReader.close();
                    return sb.toString();
                } catch (Throwable throwable) {
                    Log.e("getCPUInfo", "Could not readByteBuffer from file /proc/cpuinfo", throwable);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("getCPUInfo", "Could not open file /proc/cpuinfo", e);
        }

        return "";
    }

    public static String getNetworkOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = null;
        if(hasPermission(context, "android.permission.READ_PHONE_STATE")) {
            networkOperator = telephonyManager.getNetworkOperator();
        }

        return networkOperator;
    }

    public static String getNetworkOperatorName(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            if(hasPermission(context, "android.permission.READ_PHONE_STATE") && telephonyManager != null) {
                return telephonyManager.getNetworkOperatorName();
            }
        } catch (Throwable throwable) {
        }

        return "";
    }

    private static String getWifiMacAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(hasPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return wifiInfo.getMacAddress();
            } else {
                return "";
            }
        } catch (Throwable t) {
            return "";
        }
    }

    private static String getTelephonyDeviceID(Context context) {
        String telephonyID = "";
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager != null) {
            try {
                if(hasPermission(context, "android.permission.READ_PHONE_STATE")) {
                    telephonyID = telephonyManager.getDeviceId();
                }
            } catch (Throwable t) {
            }
        }

        return telephonyID;
    }


    private static boolean readDiskstats(){
        try {
            String[] cmd = new String[]{
                    "/system/bin/cat", "/proc/diskstats"
            };
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream is = process.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bf = new byte[1024];
            int ret;
            while ((ret = is.read()) != -1) {
                baos.write(bf, 0, ret);
            }

            String s = new String(baos.toByteArray(),"ascii");
            return s.contains("mmcblk0");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
