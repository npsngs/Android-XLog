package com.npsngs.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;


import com.forthe.xlog.XLog;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_sendLog).setOnClickListener(this);
        findViewById(R.id.btn_crash1).setOnClickListener(this);
        findViewById(R.id.btn_crash2).setOnClickListener(this);
        findViewById(R.id.btn_crash3).setOnClickListener(this);

        findViewById(R.id.btn_start_test).setOnClickListener(this);
        findViewById(R.id.btn_stop_test).setOnClickListener(this);

        XLog.init(this, getSaveDir());
    }

    private String getSaveDir(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/NDebug";
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open:
                XLog.show(this);
                break;
            case R.id.btn_sendLog:
                XLog.e("test",".view.ViewPager.setCurrentItem(ViewPager.java:562)\n" +
                        "11-03 [{\"key\":\"xiaoming\"},{\"value\":\"hahayixiao\",\"name\":\"xxoo\"},{x}]18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu.autoRun(HomeBannerVu.java:172)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu$1.run(HomeBannerVu.java:63)\n" +
                        "11-03 18:27:43.042 14150-{x}141{{{{}50/com.yxd.{\"bb\":{\"s\":\"http://www.baidu.com\"},\"aa\":\"c\"}live W/System.err:     at android.os.http://www.sss.com?ss1=1223,Handler.handleCallback(Handler.java:739)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:95)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/http://www.baidu.com System.err:     at android.os.Looper.loop(Looper.java:148)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live{\"aa\":\"xx\"}W/System.err:     at android.app");
                XLog.d("test","test send log d");
                XLog.w("test","test send log w");
                XLog.show(this, XLog.PAGE_LOGS);
                try {
                    JSONObject j = new JSONObject("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_crash1:
                int a = 0/0;
                break;
            case R.id.btn_crash2:
                throw new IllegalAccessError("Test Crash2");
            case R.id.btn_crash3:
                throw new IllegalStateException("Test Crash3");
            case R.id.btn_start_test:
                isTestON = true;
                new TestThread(1).start();
                new TestThread(2).start();
                new TestThread(3).start();
                new TestThread(4).start();
                new TestThread(5).start();
                new TestThread(6).start();
                new TestThread(7).start();
                new TestThread(8).start();
                new TestThread(9).start();
                XLog.show(this, XLog.PAGE_LOGS);
                break;
            case R.id.btn_stop_test:
                isTestON = false;
                XLog.show(this, XLog.PAGE_LOGS);
                break;
        }
    }

    class TestThread extends Thread{
        private int index;
        private int count = 0;
        public TestThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            while (isTestON){
                switch (index%3){
                    case 0:
                        XLog.d(String.format("[Thread:%d] --- %d", index, count));
                        break;
                    case 1:
                        XLog.w(String.format("[Thread:%d] --- %d", index, count));
                        break;
                    case 2:
                        XLog.e(String.format("[Thread:%d] --- %d", index, count));
                        break;
                }

                count++;

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isTestON = false;
}
