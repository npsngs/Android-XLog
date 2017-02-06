package com.forthe.xlog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

abstract class TextParseEngine implements Runnable{
    private List<TextParser> parsers;
    private Handler handler;
    public TextParseEngine() {
        parsers = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                onHandleMessage(msg);
            }
        };
    }

    private final int MSG_FINISH = 1;
    private final int MSG_UPDATE = 2;
    private final int MSG_CANCEL = 3;
    private void onHandleMessage(Message msg){
        switch (msg.what){
            case MSG_FINISH:
                thread = null;
                break;
            case MSG_CANCEL:
                thread = null;
                break;
            case MSG_UPDATE:
                onUpdateParseResult(spannableBuilder);
                break;
        }
    }

    abstract void onUpdateParseResult(SpannableStringBuilder spannableBuilder);

    public boolean add(TextParser object) {
        return parsers.add(object);
    }

    private Thread thread;
    private SpannableStringBuilder  spannableBuilder;
    private String inputStr;
    public boolean startParse(String inputStr){
        if(TextUtils.isEmpty(inputStr)){
            return false;
        }

        if(null != thread){
            return false;
        }

        this.inputStr = inputStr;
        this.spannableBuilder = new SpannableStringBuilder(inputStr);
        thread = new Thread(TextParseEngine.this);
        thread.start();
        return true;
    }


    @Override
    public void run() {
        for(TextParser parser:parsers){
            try{
                parser.parse(spannableBuilder, inputStr);
                handler.sendEmptyMessage(MSG_UPDATE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        handler.sendEmptyMessage(MSG_FINISH);
    }
}
