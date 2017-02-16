package com.forthe.xlog.panel;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.core.Container;
import com.forthe.xlog.core.LogNotifier;
import com.forthe.xlog.core.Panel;
import com.forthe.xlog.frame.PanelBase;
import com.forthe.xlog.frame.XLogNotifier;
import com.forthe.xlog.tools.TouchListView;
import com.forthe.xlog.view.LogAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


class LocalLogPanel extends PanelBase {
    private String filePath;
    private HistoryFilterPanel filterPanel;
    private LocalLogAdapter adapter;
    LocalLogPanel(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.forthe_xlog_singlelist_title, parent, false);
        TouchListView listView = (TouchListView) root.findViewById(R.id.lv);
        listView.setStackFromBottom(true);
        listView.setDownTouchListener(new TouchListView.OnDownTouchListener() {
            @Override
            public void onDownTouch() {
                if(null != filterPanel && filterPanel.isShow()){
                    filterPanel.dismiss();
                }
            }
        });
        adapter = new LocalLogAdapter(context);
        listView.setAdapter(adapter);


        TextView tv_title_center = (TextView) root.findViewById(R.id.tv_title_center);
        tv_title_center.setText(parseFileName(filePath));
        TextView tv_title_left = (TextView) root.findViewById(R.id.tv_title_left);
        tv_title_left.setText("filter ▽");
        tv_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == filterPanel){
                    filterPanel = new HistoryFilterPanel(Panel.MODE_FRIENDLY, adapter);
                }
                if (!filterPanel.isShow()) {
                    showPanel(filterPanel);
                } else {
                    filterPanel.dismiss();
                }
            }
        });
        return root;
    }

    @Override
    protected void onAttach(Container container) {
        super.onAttach(container);
        adapter.setContainer(container);
        adapter.loadLocalLog();
    }


    private class LocalLogAdapter extends LogAdapter{
        private LogNotifier notifier;

        private LocalLogAdapter(Context mContext) {
            super(mContext);
            init();
        }

        @Override
        protected String getTypeTag(String log) {
            if(TextUtils.isEmpty(log) || log.length() < 11){
                return "";
            }
            return log.substring(11,12);
        }

        private void init() {
            notifier = new XLogNotifier() {
                @Override
                protected void onNotifyLogAdd(String log) {
                    addHeaderData(log);
                }

                @Override
                protected void onNotifyLogClear() {
                    clear();
                }
            };
        }

        void loadLocalLog(){
            new Thread(){
                @Override
                public void run() {
                    File f = new File(filePath);
                    if(!f.exists() || !f.canRead()){
                        return;
                    }

                    FileInputStream is = null;
                    InputStreamReader isr = null;
                    BufferedReader in = null;
                    try {
                        is =new FileInputStream(f);
                        isr = new InputStreamReader(is);
                        in = new BufferedReader(isr);

                        StringBuilder sb = new StringBuilder();
                        String line;
                        while((line = in.readLine()) != null && getStatus() != Panel.STATUS_DETACH){
                            if(line.matches("^\\[[\\d]{2}:[\\d]{2}:[\\d]{2}\\][\\w\\w]*")){
                                if(sb.length() > 0){
                                    notifier.onLogAdd(sb.toString());
                                    sb = new StringBuilder();
                                    sb.append(line).append("\t");
                                }else{
                                    sb.append(line).append("\t");
                                }
                            }else{
                                sb.append(line);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        try{
                            in.close();
                            isr.close();
                            is.close();
                        }catch (Exception ignored){
                        }
                    }
                }
            }.start();
        }
    }

    private String parseFileName(String filePath){
        if(!TextUtils.isEmpty(filePath)){
            int p = filePath.lastIndexOf("/");
            if(filePath.length() - 1 > p){
                return filePath.substring(p+1).replace(".txt","");
            }
        }
        return "unknown file";
    }

}
