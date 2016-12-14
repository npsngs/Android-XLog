package com.npsngs.debug;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseIntArray;
import android.view.View;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class JsonParser implements TextParser {
    private Pattern pattern;
    JsonParser() {
        pattern = Pattern.compile("\\{[\\s\\S]+\\}");
    }

    @Override
    public void parse(SpannableStringBuilder spannableBuilder, String inputStr) {
        Matcher matcher = pattern.matcher(inputStr);
        while (matcher.find()){
            int start = matcher.start(0);
            String json = matcher.group(0);

            SparseIntArray index = detectJson(json);
            if(index != null && index.size() > 0){
                int startPosition;
                int endPosition;
                for(int i = 0; i < index.size(); i++){
                    startPosition = index.keyAt(i);
                    endPosition = index.get(startPosition);
                    String subJson = json.substring(startPosition, endPosition);
                    try{
                        new JSONObject(subJson);
                    }catch (Exception e){
                        continue;
                    }

                    spannableBuilder.setSpan(new JsonClickSpan(subJson), startPosition+start, endPosition+start, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private SparseIntArray detectJson(String input){
        if(TextUtils.isEmpty(input)){
            return null;
        }

        SparseIntArray index = new SparseIntArray();
        int level = 0;
        int start = 0;
        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            switch (c){
                case '{':
                    level++;
                    if(1 == level){
                        start = i;
                    }
                    break;
                case '}':
                    if(level > 0){
                        level--;

                        if(0 == level){
                            index.put(start, i+1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        level = 0;
        int end = input.length()-1;
        for(int i = end; i >= 0; i--){
            char c = input.charAt(i);
            switch (c){
                case '}':
                    level++;
                    if(1 == level){
                        end = i;
                    }
                    break;
                case '{':
                    if(level > 0){
                        level--;

                        if(0 == level){
                            index.put(i, end+1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return index;
    }


    private class JsonClickSpan extends ClickableSpan {
        private String json;
        JsonClickSpan(String json) {
            this.json = json;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(0xff009900);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            onJsonClick(json);
        }
    }

    protected abstract void onJsonClick(String json);
}
