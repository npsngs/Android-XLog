package com.npsngs.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.forthe.download.Downloader;

public class DownloadCoreActivity extends Activity{
//    String url = "http://a5.pc6.com/pc6_soure/2016-2/UVlEK7Qrz68Y8Pv2.apk";
//    String url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
//    String url = "http://116.224.86.18/apk.r1.market.hiapk.com/data/upload/apkres/2017/6_16/14/com.tencent.tmgp.sgame_025315.apk?wsiphost=local";
    String url = "http://222.73.50.172/apk.r1.market.hiapk.com/data/upload/apkres/2017/3_14/9/com.yjf.nqa_093008.apk?wsiphost=local";
    TextView tv_download, tv_info;
    private Downloader downloader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        tv_download = (TextView) findViewById(R.id.tv_download);
        tv_info = (TextView) findViewById(R.id.tv_info);
        CallBacks callBacks = new CallBacks();
        downloader = Downloader
                .createBuilder(url, getTargetFile())
                .setDebugListener(callBacks)
                .setDownloadListener(callBacks)
                .setSpeedChangeListener(callBacks)
                .build();
        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStart){
                    downloader.stop();
                }else{
                    downloader.startAsync();
                }
            }
        });
    }

    private String getTargetFile(){
        return Environment.getExternalStorageDirectory()+"/TestDownload/targetfile.apk";
    }

    private boolean isStart = false;
    class CallBacks implements
            Downloader.DebugListener,
            Downloader.DownloadListener,
            Downloader.SpeedChangeListener{

        @Override
        public void onDebug(String debugMsg) {
            DownloadCoreActivity.this.debugMsg = debugMsg;
            update();
        }


        @Override
        public void onStart(long offsetByte, long totalByte) {
            isStart = true;
            DownloadCoreActivity.this.completeSize = offsetByte;
            DownloadCoreActivity.this.totalSize = totalByte;
            update();
        }

        @Override
        public void onFinish(long totalByte) {
            isStart = false;
            DownloadCoreActivity.this.debugMsg = "finished";
            DownloadCoreActivity.this.completeSize = totalByte;
            update();
        }

        @Override
        public void onError(int errorCode) {
            isStart = false;
            DownloadCoreActivity.this.errorCode = errorCode;
            update();
        }

        @Override
        public void onProgress(long completeSize, long totalSize) {
            DownloadCoreActivity.this.completeSize = completeSize;
            DownloadCoreActivity.this.totalSize = totalSize;
            update();
        }

        @Override
        public void onStop(long offset) {
            DownloadCoreActivity.this.completeSize = offset;
            DownloadCoreActivity.this.debugMsg = "stopped";
            isStart = false;
            update();
        }

        @Override
        public void onSpeedChange(float bytesPerSec) {
            DownloadCoreActivity.this.bytesPerSec = bytesPerSec;
            update();
        }
    }

    private float bytesPerSec;
    private long completeSize, totalSize;
    private int errorCode;
    private String debugMsg;
    private String format = "speed: %.02fKB\n" +
                    "progress:%d/%d\n" +
                    "errorCode:%d\n"+
                    "Message:%s\n";

    private void update(){
        runOnUiThread(updateAction);

    }

    private Runnable updateAction = new Runnable() {
        @Override
        public void run() {
            tv_info.setText(String.format(format,
                    bytesPerSec,
                    completeSize,
                    totalSize,
                    errorCode,
                    debugMsg));

            tv_download.setText(isStart?"stop":"start");
        }
    };
}
