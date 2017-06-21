
package com.forthe.xhttp.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Params {
    private static final List<Params> recycleList = new Vector<>();

    public static Params optain() {
        Params params;
        if (recycleList.size() > 0) {
            params = recycleList.remove(recycleList.size() - 1);
        } else {
            params = new Params();
        }

        return params;
    }

    public static Params copy(Params from){
        Params ret = Params.optain();
        if(null != from){
            ret.copyFrom(from);
        }
        return ret;
    }
    
    private void copyFrom(Params from){
        if(null != from){
            params.putAll(from.params);
        }
    }
    
    private Params() {
        params = new HashMap<>();
    }

    private HashMap<String, String> params = null;

    public HashMap<String, String> getParams() {
        return params;
    }
    public Params add(String key, Object value) {
        if (null == value) {
            params.put(key, "");
        } else {
            params.put(key, value.toString());
        }
        return this;
    }

    public String remove(String key) {
        return params.remove(key);
    }

    public boolean has(String key) {
        return params.containsKey(key);
    }

    public String getValue(String key) {
        return params.get(key);
    }

    public List<String> sortKeys() {
        Set<String> keys = params.keySet();
        List<String> sortKeys = new ArrayList<>();
        for (String key:keys) {
            sortKeys.add(key);
        }
        Collections.sort(sortKeys, keyComparator);
        return sortKeys;
    }

    public void encodeValues(String encode) {
        Set<String> keys = params.keySet();
        for (String key:keys) {
            String value = params.get(key);
            if (null != value) {
                try {
                    value = URLEncoder.encode(value, encode);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                params.put(key, value);
            }
        }
    }

    /**
     * 转换成get请求参数
     */
    public String paramsToString() {
        List<String> sortKeys = sortKeys();
        StringBuilder builder = new StringBuilder();
        for (String key : sortKeys) {
            String value = params.get(key);
            builder.append(key).append("=").append(value).append("&");
        }
        
        if(builder.length() > 0){
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public void recycle() {
        params.clear();
        recycleList.add(this);
    }

    private static final Comparator<String> keyComparator = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            if (lhs.equals("source")) {
                return 1;
            } else if (rhs.equals("source")) {
                return -1;
            }

            return lhs.compareTo(rhs);
        }
    };

    @Override
    public String toString() {
        return params.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Params) {
            Params obj = (Params) o;
            HashMap<String, String> newParams = obj.params;
            return params.equals(newParams);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return params.hashCode();
    }
    
}
