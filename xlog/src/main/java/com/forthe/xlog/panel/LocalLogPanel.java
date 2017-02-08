package com.forthe.xlog.panel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.forthe.xlog.R;
import com.forthe.xlog.core.Container;
import com.forthe.xlog.core.LogNotifier;
import com.forthe.xlog.core.Panel;
import com.forthe.xlog.frame.PanelBase;
import com.forthe.xlog.frame.XLogNotifier;
import com.forthe.xlog.view.LogAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class LocalLogPanel extends PanelBase {
    private String filePath;
    private ListView listView;
    private LocalLogAdapter adapter;
    public LocalLogPanel(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        listView = (ListView) inflater.inflate(R.layout.forthe_xlog_singlelist, parent, false);
        return listView;
    }

    @Override
    protected void onAttach(Container container) {
        super.onAttach(container);
        adapter = new LocalLogAdapter(container.getContainer().getContext(), container);
        listView.setAdapter(adapter);
        adapter.loadLocalLog();
    }


    private class LocalLogAdapter extends LogAdapter{
        private LogNotifier notifier;

        public LocalLogAdapter(Context mContext, Container container) {
            super(mContext, container);
            notifier = new XLogNotifier() {
                @Override
                protected void onNotifyLogAdd(String log) {
                    addData(log);
                }

                @Override
                protected void onNotifyLogClear() {
                    clear();
                }
            };
        }

        public void loadLocalLog(){
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
                        }catch (Exception e){
                        }
                    }
                }
            }.start();
        }
    }
}
