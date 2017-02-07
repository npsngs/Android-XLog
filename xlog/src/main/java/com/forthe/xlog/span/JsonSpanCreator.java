package com.forthe.xlog.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.forthe.xlog.core.SpanCreator;
import com.forthe.xlog.frame.ColorPool;

import org.json.JSONArray;
import org.json.JSONObject;


public abstract class JsonSpanCreator implements SpanCreator {
    @Override
    public Object createSpan(String source, int startPosition, int endPosition) {
        String json = source.substring(startPosition,endPosition);
        try{
            if(json.startsWith("{")) {
                new JSONObject(json);
            } else if(json.startsWith("[")) {
                new JSONArray(json);
            } else {
                return null;
            }
        }catch (Exception e){
            return null;
        }
        return new JsonClickSpan(json);
    }

    private class JsonClickSpan extends ClickableSpan {
        private String json;
        JsonClickSpan(String json) {
            this.json = json;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ColorPool.json_color);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            onJsonClick(json);
        }
    }

    protected abstract void onJsonClick(String json);
}
