package com.forthe.xlog;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseIntArray;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

class JsonParser implements TextParser {
    private PanelContainer panelContainer;

    JsonParser(PanelContainer panelContainer) {
        this.panelContainer = panelContainer;
    }

    @Override
    public void parse(SpannableStringBuilder spannableBuilder, String inputStr) {
        SparseIntArray index = detectJson(inputStr);
        if(index != null && index.size() > 0){
            int startPosition;
            int endPosition;
            for(int i = 0; i < index.size(); i++){
                startPosition = index.keyAt(i);
                endPosition = index.get(startPosition);
                String subJson = inputStr.substring(startPosition, endPosition);
                try{
                    if(subJson.startsWith("{")) {
                        new JSONObject(subJson);
                    } else if(subJson.startsWith("[")) {
                        new JSONArray(subJson);
                    } else {
                        continue;
                    }
                }catch (Exception e){
                    continue;
                }

                spannableBuilder.setSpan(new JsonClickSpan(subJson), startPosition, endPosition, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
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
        int arrayLevel = 0;
        int arrayStart = 0;
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
                case '[':
                    arrayLevel++;
                    if(1 == arrayLevel){
                        arrayStart = i;
                    }
                    break;
                case ']':
                    if(arrayLevel > 0){
                        arrayLevel--;

                        if(0 == arrayLevel){
                            index.put(arrayStart, i+1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        level = 0;
        arrayLevel = 0;
        int end = input.length()-1;
        int arrayEnd = end;
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
                case '[':
                    arrayLevel++;
                    if(1 == arrayLevel){
                        arrayEnd = i;
                    }
                    break;
                case ']':
                    if(arrayLevel > 0){
                        arrayLevel--;

                        if(0 == arrayLevel){
                            index.put(i, arrayEnd+1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        //remove overlap
        /*
        for(int i = 0;i< index.size();i++){
            int startI = index.keyAt(i);
            int endI = index.get(startI);
            for(int j = i+1;j< index.size();j++){
                int startJ = index.keyAt(j);
                int endJ = index.get(startJ);
                if(endI < startJ || startI > endJ){//互相独立
                    continue;
                }else if(endI >= endJ && startI < startJ){//I 包含 J
                    index.removeAt(j);
                    j--;
                }else if(endI <= endJ && startI > startJ){//J 包含 I
                    index.removeAt(i);
                    i--;
                    break;
                }else if(endI > startJ || endJ > startI){//互相交叉
                    index.removeAt(j);
                    index.removeAt(i);
                    i--;
                    break;
                }
            }
        }
        */

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
            if(panelContainer != null){
                JsonPanel jsonPanel = new JsonPanel(json);
                panelContainer.showPanel(jsonPanel);
            }
        }
    }

}
