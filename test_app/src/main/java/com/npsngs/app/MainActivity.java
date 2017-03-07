package com.npsngs.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


import com.forthe.xlog.XLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener{
    View v;
    MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v = findViewById(R.id.btn_open);
        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_sendLog).setOnClickListener(this);
        findViewById(R.id.btn_crash1).setOnClickListener(this);
        findViewById(R.id.btn_crash2).setOnClickListener(this);
        findViewById(R.id.btn_crash3).setOnClickListener(this);

        findViewById(R.id.btn_start_test).setOnClickListener(this);
        findViewById(R.id.btn_stop_test).setOnClickListener(this);
        findViewById(R.id.btn_start_activity).setOnClickListener(this);

        XLog.init(this, getSaveDir());
        XLog.addSwitchItem("isTest");
        XLog.i("start main");
        activity = this;
    }

    private String getSaveDir(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/XLog";
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
                XLog.i("test",".view.ViewPager.setCurrentItem(ViewPager.java:562)\n" +
                        "11-03 [{\"key\":\"xiaoming\"},{\"value\":\"hahayixiao\",\"name\":\"xxoo\"},{x}]18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu.autoRun(HomeBannerVu.java:172)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu$1.run(HomeBannerVu.java:63)\n" +
                        "11-03 1\",\"downloadUrl\":\"http://xiazai.52duoyou.com/download/test20161207/GrnFuy7EfDB58bDzdg3eHiqyNJV5Zbdy.apk\",8:27:43.042 14150-{x}141{{{{}50/com.yxd.{\"bb\":{\"s\":\"http://www.baidu.com\"},\"aa\":\"c\"}live W/System.err:     at android.os.http://www.sss.com?ss1=1223,httpler.handleCallback(Handler.java:739)\n" +
                        "11-03 18:27http://h.hiphotos.baidu.com/zhidao/pic/item/6d81800a19d8bc3ed69473cb848ba61ea8d34516.jpg}:43.042 14150-14150/com.yxd.live W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:95)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live W/http://www.baidu.com,\n System.err:     at android.os.Looper.loop(Looper.java:148)\n" +
                        "11-03 18:27:43.042 14150-14150/com.yxd.live{\"aa\":\"xx\"}W/System.err:     at android.app");
                XLog.d("POST\t[game/app_home_getGameByType]\t[url:http://duoyou.youxiduo.com/service/game/app_home_getGameByType?appname=duoyou_android&channel=bf-2WlIucl1m&channelId=bf-2WlIucl1m&gameTypeTag=1&idcode=357485035080912&pageNow=1&pageSize=9&platform=sdk&session_id=&version=3.1.5&source=a63a9599b2c5ec4ad166051ffb9936c0]\n" +
                        "[response:200]:{\"errorCode\":0,\"errhttp://h.hiphotos.baidu.com/zhidao/pic/item/6d81800a19d8bc3ed69473cb848ba61ea8d34516.jpg)orDescription\":\"Success\",\"result\":[{\"gameId\":\"aG9SZN3RDG3L\",\"gameName\":\"舞动青春\",\"gameSize\":147,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/201701202104305CRJ.png\",\"gameDiscount\":53.0,\"chargeDiscount\":53.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"舞蹈\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160913201405SxNo.JPG\",\"gameListOrder\":760,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,音乐\"},{\"gameId\":\"SW_-Kr0FitBF\",\"gameName\":\"劲舞团\",\"gameSize\":389,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/12/20161221131226qLxx.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"舞蹈\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160919155744VjNK.jpg\",\"gameListOrder\":500,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,音乐\"},{\"gameId\":\"LDT7P0Ph9X1r\",\"gameName\":\"阴阳师\",\"gameSize\":518,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160923104530eYxx.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"卡牌\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160923104531m8ny.jpg\",\"gameListOrder\":150,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,策略\"},{\"gameId\":\"KdjSl5J9q84x\",\"gameName\":\"天堂2\",\"gameSize\":528,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/201701072242468Tau.png\",\"gameDiscount\":74.0,\"chargeDiscount\":74.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160928180231iBbj.jpg\",\"gameListOrder\":150,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"},{\"gameId\":\"1OyYapIQ54dH\",\"gameName\":\"倩女幽魂\",\"gameSize\":300,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/12/20161230161558oDLT.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/12/20161230161558pOYG.jpg\",\"gameListOrder\":130,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"},{\"gameId\":\"Upe84A_uD8WG\",\"gameName\":\"九阴\",\"gameSize\":400,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/20170119100337lIvA.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/20170119100337JOjY.jpg\",\"gameListOrder\":100,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"},{\"gameId\":\"X1jMjCrov-eA\",\"gameName\":\"皇室战争\",\"gameSize\":300,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/201609140041458GsP.png\",\"gameDiscount\":76.0,\"chargeDiscount\":76.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"策略\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/201609140041451A9S.JPG\",\"gameListOrder\":10,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,策略\"},{\"gameId\":\"A1iwvJ6Z2o-x\",\"gameName\":\"水浒Q传\",\"gameSize\":211,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/20170107143900Yi3R.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"回合\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160927125941duoU.jpg\",\"gameListOrder\":6,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,回合\"},{\"gameId\":\"Lla4ukWCaP08\",\"gameName\":\"剑与魔法\",\"gameSize\":292,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/02/2017020512111450UH.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/02/20170205121114iGJb.jpg\",\"gameListOrder\":5,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"}],\"totalCount\":10}\n");
                XLog.w("test","test send log w");
                XLog.showLog(this);
                try {
                    JSONObject j = new JSONObject("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                List<String> ss = new ArrayList<>();
                ss.add(null);
                ss.add("ssdd");
                ss.add("null");
                ss.add("ssjhhjjj");
                ss.add("ppppppppp");
                XLog.d(ss);

                Map<String,String> maps = new HashMap<>();
                maps.put("a","nullable");
                maps.put("ab","ss");
                maps.put("aa","sskjk");
                maps.put("ac",null);
                maps.put("af","null");
                maps.put(null,"aa");
                maps.put("ad","nnnnull");
                XLog.d(maps);
                XLog.d(this);

                Object[][][] ab = new Object[][][]{
                        {null,{2,3.0f,this,6.7897f}},{{4.123f,5,this,1.23f},{3,new TextView(this),5,8.9f}}
                };
                XLog.d(ab);
                XLog.d(ab[0]);
                XLog.d(ab[0][1]);
                break;
            case R.id.btn_crash1:
                try{
                    int a = 0/0;
                }catch (Throwable t){
                    XLog.e(t);
                }
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
                XLog.showLog(this);
                break;
            case R.id.btn_stop_test:
                isTestON = false;
                XLog.showLog(this);
                break;
            case R.id.btn_start_activity:
                startActivity(new Intent(this, MainActivity.class));
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
                switch (index%4){
                    case 0:
                        XLog.d(String.format("[Thread:%d] --- %d", index, count));
                        break;
                    case 1:
                        XLog.w(String.format("[Thread:%d] --- %d", index, count));
                        break;
                    case 2:
                        XLog.e(String.format("[Thread:%d] --- %d", index, count));
                        break;
                    case 3:
                        XLog.e("test",".view.ViewPager.setCurrentItem(ViewPager.java:562)\n" +
                                "11-03 [{\"key\":\"xiaoming\"},{\"value\":\"hahayixiao\",\"name\":\"xxoo\"},{x}]18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu.autoRun(HomeBannerVu.java:172)\n" +
                                "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at com.yxd.live.vu.modules.HomeBannerVu$1.run(HomeBannerVu.java:63)\n" +
                                "11-03 18:27:43.042 14150-{x}141{{{{}50/com.yxd.{\"bb\":{\"s\":\"http://www.baidu.com\"},\"aa\":\"c\"}live W/System.err:     at android.os.http://www.sss.com?ss1=1223,Handler.handleCallback(Handler.java:739)\n" +
                                "11-03 18:27:43.042 14150-14150/com.yxd.live W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:95)\n" +
                                "11-03 18:27:43.042 14150-14150/com.yxd.live W/http://www.baidu.com System.err:     at android.os.Looper.loop(Looper.java:148)\n" +
                                "11-03 18:27:43.042 14150-14150/com.yxd.live{\"aa\":\"xx\"}W/System.err:     at android.app");
                        XLog.d("POST\t[game/app_home_getGameByType]\t[url:http://duoyou.youxiduo.com/service/game/app_home_getGameByType?appname=duoyou_android&channel=bf-2WlIucl1m&channelId=bf-2WlIucl1m&gameTypeTag=1&idcode=357485035080912&pageNow=1&pageSize=9&platform=sdk&session_id=&version=3.1.5&source=a63a9599b2c5ec4ad166051ffb9936c0]\n" +
                                "[response:200]:{\"errorCode\":0,\"errorDescription\":\"Success\",\"result\":[{\"gameId\":\"aG9SZN3RDG3L\",\"gameName\":\"舞动青春\",\"gameSize\":147,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/201701202104305CRJ.png\",\"gameDiscount\":53.0,\"chargeDiscount\":53.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"舞蹈\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160913201405SxNo.JPG\",\"gameListOrder\":760,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,音乐\"},{\"gameId\":\"SW_-Kr0FitBF\",\"gameName\":\"劲舞团\",\"gameSize\":389,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/12/20161221131226qLxx.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"舞蹈\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160919155744VjNK.jpg\",\"gameListOrder\":500,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,音乐\"},{\"gameId\":\"LDT7P0Ph9X1r\",\"gameName\":\"阴阳师\",\"gameSize\":518,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160923104530eYxx.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"卡牌\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160923104531m8ny.jpg\",\"gameListOrder\":150,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,策略\"},{\"gameId\":\"KdjSl5J9q84x\",\"gameName\":\"天堂2\",\"gameSize\":528,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/201701072242468Tau.png\",\"gameDiscount\":74.0,\"chargeDiscount\":74.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160928180231iBbj.jpg\",\"gameListOrder\":150,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"},{\"gameId\":\"1OyYapIQ54dH\",\"gameName\":\"倩女幽魂\",\"gameSize\":300,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/12/20161230161558oDLT.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/12/20161230161558pOYG.jpg\",\"gameListOrder\":130,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"},{\"gameId\":\"Upe84A_uD8WG\",\"gameName\":\"九阴\",\"gameSize\":400,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/20170119100337lIvA.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/20170119100337JOjY.jpg\",\"gameListOrder\":100,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"},{\"gameId\":\"X1jMjCrov-eA\",\"gameName\":\"皇室战争\",\"gameSize\":300,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/201609140041458GsP.png\",\"gameDiscount\":76.0,\"chargeDiscount\":76.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"策略\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/201609140041451A9S.JPG\",\"gameListOrder\":10,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,策略\"},{\"gameId\":\"A1iwvJ6Z2o-x\",\"gameName\":\"水浒Q传\",\"gameSize\":211,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/01/20170107143900Yi3R.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"回合\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2016/09/20160927125941duoU.jpg\",\"gameListOrder\":6,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,回合\"},{\"gameId\":\"Lla4ukWCaP08\",\"gameName\":\"剑与魔法\",\"gameSize\":292,\"gameIcon\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/02/2017020512111450UH.png\",\"gameDiscount\":54.0,\"chargeDiscount\":54.0,\"chargequota\":null,\"rechargequota\":null,\"gameType\":\"角色\",\"gameListPicture\":\"http://img.youxiduo.com/userdirs/duoyou_game/2017/02/20170205121114iGJb.jpg\",\"gameListOrder\":5,\"gameCreateTime\":null,\"isTop\":\"0\",\"gameTypeTag\":\"推荐,角色\"}],\"totalCount\":10}\n");
                        XLog.w("test","test send log w");
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
    ATest aTest = new ATest();
    class ATest{
        String a = "xx";
        Handler handler = new Handler();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_VOLUME_UP){
            XLog.show(this);
            return true;
        }else if(keyCode == event.KEYCODE_VOLUME_DOWN){
            XLog.showLog(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
