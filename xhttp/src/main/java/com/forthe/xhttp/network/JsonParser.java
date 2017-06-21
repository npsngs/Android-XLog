package com.forthe.xhttp.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class JsonParser<T> {

    public abstract T parse(JSONObject json) throws Exception;

    public List<T> parse(JSONArray array) throws Exception{
        if(null != array){
            List<T> lists = new ArrayList<>();
            for(int i=0;i < array.length();i++){
                JSONObject jsonObject = array.getJSONObject(i);
                if(null == jsonObject){
                    continue;
                }
                try{
                    T t = parse(jsonObject);
                    lists.add(t);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return lists;
        }
        return null;
    }
}
