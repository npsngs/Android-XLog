package com.forthe.xlog.frame;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseIntArray;

import com.forthe.xlog.core.LogParser;
import com.forthe.xlog.core.SpanCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParseEngine{
    public void startParse(String inputStr, OnParseCallback callback){
        if(TextUtils.isEmpty(inputStr) && callback == null){
            return;
        }

        ParserWorker parserWorker = new ParserWorker(inputStr,callback);
        executorService.execute(parserWorker);
    }


    private List<ParserItem> parsers;
    private Handler handler;
    private ExecutorService executorService;

    public ParseEngine() {
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                onHandleMessage(msg);
            }
        };
    }

    private final int MSG_FINISH = 1;
    private final int MSG_UPDATE = 2;
    private void onHandleMessage(Message msg){
        switch (msg.what){
            case MSG_FINISH:
                ((ParserWorker) msg.obj).onParseUpdate();
                break;
            case MSG_UPDATE:
                ((ParserWorker) msg.obj).onParseUpdate();
                break;
        }
    }

    public void addParser(LogParser parser, SpanCreator spanCreator) {
        if(parsers == null){
            parsers = new ArrayList<>();
        }

        parsers.add(new ParserItem(parser, spanCreator));
    }

    public interface OnParseCallback{
        void onParseUpdate(String source, SpannableStringBuilder stringBuilder);
    }

    private class ParserItem{
        LogParser parser;
        SpanCreator spanCreator;
        ParserItem(LogParser parser, SpanCreator spanCreator) {
            this.parser = parser;
            this.spanCreator = spanCreator;
        }
    }


    private class ParserWorker implements Runnable{
        private SpannableStringBuilder spannableBuilder;
        private String inputStr;
        private OnParseCallback callback;
        private boolean isLazyUpdate = false;

        ParserWorker(String inputStr, OnParseCallback callback) {
            this.inputStr = inputStr;
            this.callback = callback;
            this.spannableBuilder = new SpannableStringBuilder(inputStr);
            if(inputStr.length() > 1024*50){
                isLazyUpdate = true;
            }
        }

        @Override
        public void run() {
            for(ParserItem item:parsers){
                LogParser parser = item.parser;
                try{
                    SparseIntArray matchResult = parser.parse(inputStr);
                    if(matchResult != null && matchResult.size() > 0){
                        SpanCreator spanCreator = item.spanCreator;
                        createSpan(spanCreator, matchResult);
                    }
                    if(!isLazyUpdate){
                        handler.obtainMessage(MSG_UPDATE, this).sendToTarget();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(isLazyUpdate){
                handler.obtainMessage(MSG_FINISH, this).sendToTarget();
            }
        }

        private void createSpan(SpanCreator spanCreator, SparseIntArray matchResult){
            int startPosition;
            int endPosition;
            for(int i = 0; i < matchResult.size(); i++) {
                startPosition = matchResult.keyAt(i);
                endPosition = matchResult.get(startPosition);
                Object spanObj = spanCreator.createSpan(inputStr, startPosition, endPosition);
                if (null != spanObj) {
                    spannableBuilder.setSpan(spanObj, startPosition, endPosition, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }

        void onParseUpdate(){
            callback.onParseUpdate(inputStr, spannableBuilder);
        }
    }
}
