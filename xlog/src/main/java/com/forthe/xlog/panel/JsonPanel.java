package com.forthe.xlog.panel;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.frame.PanelBase;
import com.forthe.xlog.frame.ParseEngine;
import com.forthe.xlog.parser.URLParser;
import com.forthe.xlog.span.URLSpanCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Iterator;

class JsonPanel extends PanelBase {
    private String json;
    JsonPanel(String json) {
        this.json = json;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.forthe_xlog_json_panel, parent, false);
        final TextView tv_message = (TextView) v.findViewById(R.id.tv_message_detail);
        tv_message.setMovementMethod(LinkMovementMethod.getInstance());
        ParseEngine parseEngine = new ParseEngine();
        parseEngine.addParser(new URLParser(), new URLSpanCreator(){
            @Override
            protected void gotoHttpPanel(String url) {
                showPanel(new HttpPanel(url));
            }
        });

        try {
            String formatJson = formatResult(json).toString();
            parseEngine.startParse(formatJson, new ParseEngine.OnParseCallback() {
                @Override
                public void onParseUpdate(String source, SpannableStringBuilder stringBuilder) {
                    tv_message.setText(stringBuilder);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            tv_message.setText(json);
        }
        return v;
    }

    private StringBuilder formatResult(String json) throws JSONException{
        if(TextUtils.isEmpty(json)){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if(json.startsWith("{") && json.endsWith("}")){
            JSONObject jsonObject = new JSONObject(new JSONTokener(json));
            formatJsonObject("", jsonObject, builder);
        }else if(json.startsWith("[") && json.endsWith("]")){
            JSONArray array = new JSONArray(new JSONTokener(json));
            formatJsonArray("", array, builder);
        }else{
            builder.append(json);
        }
        return builder;
    }

    private void formatJsonObject(String space, JSONObject object, StringBuilder builder) throws JSONException {
        builder.append(space).append("{\n");
        space = space+"\t";
        if(null != object){
            if(object.length() > 0){
                Iterator<?> keys = object.keys();
                while(keys.hasNext()){
                    String key = (String) keys.next();
                    builder.append(space).append(key).append(":");
                    Object value = object.get(key);
                    if(null != value){
                        if(value instanceof JSONObject){
                            formatJsonObject(space, (JSONObject) value, builder);
                        }else if(value instanceof JSONArray){
                            formatJsonArray(space, (JSONArray) value, builder);
                        }else if(value instanceof CharSequence){
                            builder.append("\"").append((CharSequence) value).append("\",\n");
                        }else{
                            builder.append("\"").append(value.toString()).append("\",\n");
                        }
                    }else{
                        builder.append("null,\n");
                    }
                }
            }
        }

        if(!TextUtils.isEmpty(space)){
            space = space.substring(0, space.length()-1);
        }
        builder.append(space).append("}\n");
    }

    private void formatJsonArray(String space, JSONArray array, StringBuilder builder) throws JSONException{
        builder.append(space).append("[");
        space = space+"\t";
        if(null != array){
            builder.append("\n");
            if(array.length() > 0){
                Object value;
                for(int i=0;i<array.length();i++){
                    try{
                        value = array.get(i);
                    }catch (Exception e){
                        continue;
                    }

                    if(null != value){
                        if(value instanceof JSONObject){
                            formatJsonObject(space, (JSONObject) value, builder);
                        }else if(value instanceof JSONArray){
                            formatJsonArray(space, (JSONArray) value, builder);
                        }else if(value instanceof CharSequence){
                            builder.append("\"").append((CharSequence)value).append("\"");
                        }else{
                            builder.append("\"").append(value.toString()).append("\"");
                        }
                    }
                    builder.append(space).append(",\n");
                }
            }
            if(!TextUtils.isEmpty(space)){
                space = space.substring(0, space.length()-1);
            }
            builder.append(space).append("]\n");
        }else{
            builder.append(" ]\n");
        }
    }


}
