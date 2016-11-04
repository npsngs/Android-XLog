package com.npsngs.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.npsngs.debug.DEBug;

public class MainActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DEBug.init(this);
        DEBug.initCrashDebug(getCrashDir());
        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_sendLog).setOnClickListener(this);
        findViewById(R.id.btn_crash).setOnClickListener(this);

    }

    private String getCrashDir(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        DEBug.show(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open:
                DEBug.show(this);
                break;
            case R.id.btn_sendLog:
                DEBug.e("test",".view.ViewPager.setCurrentItem(ViewPager.java:562)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu.autoRun(HomeBannerVu.java:172)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu$1.run(HomeBannerVu.java:63)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at android.os.Handler.handleCallback(Handler.java:739)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:95)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at android.os.Looper.loop(Looper.java:148)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at android.app");
                DEBug.d("test","test send log d");
                DEBug.w("test","test send log w");
                DEBug.show(this, DEBug.PAGE_LOGS);
                break;
            case R.id.btn_crash:
                int a = 0/0;
                break;
        }
    }
}
