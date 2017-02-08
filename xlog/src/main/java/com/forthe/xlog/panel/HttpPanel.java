package com.forthe.xlog.panel;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.Adapter;
import com.forthe.xlog.frame.PanelBase;
import com.forthe.xlog.tools.XLogUtils;

import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpPanel extends PanelBase implements View.OnClickListener{
    private String url;
    private ListView lv_method, lv_accept, lv_content, lv_parameter;
    private EditText et_host;
    private TextView tv_method, tv_accept, tv_content_type, tv_view_url,  tv_send;
    private String host_url;
    private String method = "GET";
    private String accept = "*/*";
    private String content_type = "application/x-www-form-urlencoded";
    public HttpPanel(String url) {
        this.url = url;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.forthe_xlog_http_panel, parent, false);
        lv_method = (ListView) v.findViewById(R.id.lv_method);
        lv_accept = (ListView) v.findViewById(R.id.lv_accept);
        lv_content = (ListView) v.findViewById(R.id.lv_content);
        lv_parameter = (ListView) v.findViewById(R.id.lv_parameter);

        et_host = (EditText) v.findViewById(R.id.et_host);
        tv_method = (TextView) v.findViewById(R.id.tv_method);
        tv_accept = (TextView) v.findViewById(R.id.tv_accept);
        tv_content_type = (TextView) v.findViewById(R.id.tv_content_type);
        tv_view_url = (TextView) v.findViewById(R.id.tv_view_url);
        tv_send = (TextView) v.findViewById(R.id.tv_send);
        tv_method.setOnClickListener(this);
        tv_accept.setOnClickListener(this);
        tv_content_type.setOnClickListener(this);
        tv_view_url.setOnClickListener(this);
        tv_send.setOnClickListener(this);

        MethodAdapter methodAdapter = new MethodAdapter(context);
        methodAdapter.addData("GET");
        methodAdapter.addData("POST");
        lv_method.setAdapter(methodAdapter);

        AcceptAdapter acceptAdapter = new AcceptAdapter(context);
        acceptAdapter.setData(getContentTypeList());
        lv_accept.setAdapter(acceptAdapter);

        ContentTypeAdapter contentTypeAdapter = new ContentTypeAdapter(context);
        contentTypeAdapter.addData("application/json");
        contentTypeAdapter.addData("application/x-www-form-urlencoded");
        lv_content.setAdapter(contentTypeAdapter);

        return v;
    }


    private ParameterAdapter parameterAdapter;
    @Override
    protected void onAttach(Container container) {
        super.onAttach(container);
        parameterAdapter = new ParameterAdapter(container.getContext());
        lv_parameter.setAdapter(parameterAdapter);
        Uri uri = Uri.parse(url);
        host_url = String.format("%s://%s%s",uri.getScheme(),uri.getAuthority(),uri.getPath());
        et_host.setText(host_url);
        Set<String> keys = uri.getQueryParameterNames();
        if(null == keys || keys.isEmpty()){
            lv_parameter.setVisibility(View.INVISIBLE);
        }else{
            for(String key:keys) {
                parameterAdapter.addData(new KeyValuePair(key, uri.getQueryParameter(key)));
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_send == id) {
            executeSend();
        } else if (R.id.tv_method == id) {
            if (lv_method.getVisibility() == View.VISIBLE) {
                lv_method.setVisibility(View.GONE);
            } else {
                lv_method.setVisibility(View.VISIBLE);
            }
        }else if(R.id.tv_accept == id){
            if (lv_accept.getVisibility() == View.VISIBLE) {
                lv_accept.setVisibility(View.GONE);
            } else {
                lv_accept.setVisibility(View.VISIBLE);
            }
        }else if(R.id.tv_content_type == id){
            if (lv_content.getVisibility() == View.VISIBLE) {
                lv_content.setVisibility(View.GONE);
            } else {
                lv_content.setVisibility(View.VISIBLE);
            }
        }else if(R.id.tv_view_url == id){
            XLogUtils.viewUrl(v.getContext(), url);
        }
    }


    private class ParameterAdapter extends Adapter<KeyValuePair>{
        public ParameterAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ParameterHolder holder;
            if(null == v){
                v = View.inflate(parent.getContext(), R.layout.forthe_xlog_httpparam_item, null);
                holder = new ParameterHolder(v);
                v.setTag(holder);
            }else{
                holder = (ParameterHolder) v.getTag();
            }

            holder.bind(position);
            return v;
        }


        private class ParameterHolder {
            private EditText et_key, et_value;
            public ParameterHolder(View v){
                et_key = (EditText) v.findViewById(R.id.et_key);
                et_value = (EditText) v.findViewById(R.id.et_value);
            }

            public void bind(int position){
                KeyValuePair valuePair = getItem(position);
                et_key.setText(valuePair.key);
                et_value.setText(valuePair.value);
            }
        }
    }


    private class KeyValuePair{
        String key;
        String value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public boolean isValid(){
            return !TextUtils.isEmpty(key) && value !=null;
        }
    }


    private class MethodAdapter extends OptionAdapter{
        public MethodAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected void onItemClick(int position, Adapter<String> adapter) {
            method = getItem(position);
            adapter.notifyDataSetChanged();
            lv_method.postDelayed(new Runnable() {
                @Override
                public void run() {
                    lv_method.setVisibility(View.GONE);
                }
            },600);
            if("GET".equals(method)){
                content_type = "application/x-www-form-urlencoded";
            }

            tv_view_url.setVisibility("GET".equals(method)?View.VISIBLE:View.GONE);
            tv_content_type.setVisibility("POST".equals(method)?View.VISIBLE:View.GONE);
        }

        @Override
        protected String getSelectedItem(int position) {
            return method;
        }
    }


    private class AcceptAdapter extends OptionAdapter{
        public AcceptAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected void onItemClick(int position, Adapter<String> adapter) {
            accept = getItem(position);
            adapter.notifyDataSetChanged();
            lv_accept.postDelayed(new Runnable() {
                @Override
                public void run() {
                    lv_accept.setVisibility(View.GONE);
                }
            },600);
        }

        @Override
        protected String getSelectedItem(int position) {
            return accept;
        }
    }


    private class ContentTypeAdapter extends OptionAdapter{
        public ContentTypeAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected void onItemClick(int position, Adapter<String> adapter) {
            content_type = getItem(position);
            adapter.notifyDataSetChanged();
            lv_content.postDelayed(new Runnable() {
                @Override
                public void run() {
                    lv_content.setVisibility(View.GONE);
                }
            },600);
        }

        @Override
        protected String getSelectedItem(int position) {
            return content_type;
        }
    }


    private abstract class OptionAdapter extends Adapter<String>{
        int padding;
        public OptionAdapter(Context mContext) {
            super(mContext);
            padding = XLogUtils.dp2px(mContext, 10);
        }

        protected abstract void onItemClick(int position, Adapter<String> adapter);
        protected abstract String getSelectedItem(int position);
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final TextView tv;
            if(null == convertView){
                tv = new TextView(parent.getContext());
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(14f);
                tv.setPadding(0, padding,0,padding);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (int) v.getTag();
                        onItemClick(pos, OptionAdapter.this);
                    }
                });
            }else{
                tv = (TextView) convertView;
            }
            tv.setTag(position);
            String s = getItem(position);
            tv.setText(s);
            tv.setTextColor(s.equals(getSelectedItem(position))?0xff26b9f7:0xffa0a0a0);
            return tv;
        }
    }

    private void executeSend(){
        tv_send.setEnabled(false);
        new Thread(){
            @Override
            public void run() {
                String ret;
                try {
                    ret = send();
                } catch (Exception e) {
                    e.printStackTrace();
                    ret = e.getMessage();
                }

                final String finalRet = ret;
                tv_send.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_send.setEnabled(true);
                        showPanel(new TextPanel(finalRet, Color.WHITE));
                    }
                });
            }
        }.start();



    }


    private String send() throws Exception{
        StringBuilder sb = new StringBuilder("[Request>>]\n");
        Map<String, List<String>> map;
        Set<String> keys;

        HttpURLConnection conn;
        boolean isGetMethod = method.equals("GET");
        sb.append(method).append(" ").append(isGetMethod?url:host_url).append("\n");

        if(isGetMethod){
            URL url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
        }else{
            URL url = new URL(et_host.getText().toString());
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setRequestMethod(isGetMethod?"GET":"POST");
        conn.setRequestProperty("accept", accept);
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "xlog_client");
        conn.setRequestProperty("Content-Type",content_type);

        if(!isGetMethod){
            conn.setDoOutput(true);
            conn.setDoInput(true);
            List<KeyValuePair> pairs = parameterAdapter.getDataList();
            if(parameterAdapter.getCount() > 0){
                if("application/x-www-form-urlencoded".equals(content_type)){
                    StringBuilder params = new StringBuilder();
                    for(KeyValuePair pair:pairs){
                        if(pair.isValid()){
                            params.append(pair.key).append("=").append(pair.value).append("&");
                        }
                    }
                    if(params.length() > 0){
                        params.deleteCharAt(params.length()-1);
                    }
                    conn.getOutputStream().write(params.toString().getBytes());
                }else{
                    JSONStringer jsonStringer = new JSONStringer();
                    jsonStringer.object();
                    for(KeyValuePair pair:pairs){
                        if(pair.isValid()){
                            jsonStringer.key(pair.key);
                            jsonStringer.value(pair.value);
                        }
                    }
                    jsonStringer.endObject();
                    conn.getOutputStream().write(jsonStringer.toString().getBytes());
                }
            }
        }




        sb.append("\n[<<response]\n");

        conn.connect();

        map = conn.getHeaderFields();
        keys = map.keySet();
        for(String key:keys){
            if(!TextUtils.isEmpty(key)){
                sb.append(key).append(":");
            }
            List<String> values = map.get(key);
            if(null != values){
                for(String value:values){
                    sb.append(" ").append(value);
                }
            }
            sb.append("\n");
        }

        sb.append("\n");
        try{
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            is.close();
        }catch (Exception e){
            sb.append(e.getMessage());
        }

        return sb.toString();
    }

    private List<String> getContentTypeList(){
        String[] ls = new String[]{
                "*/*",
                "application/*",
                "application/json",
                "text/*",
                "text/json",
                "text/html"
        };
        return Arrays.asList(ls);
    }
}
