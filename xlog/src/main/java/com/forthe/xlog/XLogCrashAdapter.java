package com.forthe.xlog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.forthe.xlog.frame.Adapter;
import com.forthe.xlog.frame.ColorPool;
import com.forthe.xlog.frame.PanelContainer;
import com.forthe.xlog.panel.TextPanel;
import com.forthe.xlog.tools.XLogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class XLogCrashAdapter extends Adapter<XLogCrashAdapter.FileEntry> {
    private PanelContainer panelContainer;
    XLogCrashAdapter(Context mContext, PanelContainer panelContainer) {
        super(mContext);
        this.panelContainer = panelContainer;
    }

    void loadData() {
        File errLog = new File(XLog.getCrashSaveDir());
        if (!errLog.exists() || !errLog.isDirectory()) {
            return;
        }

        List<File> files = new ArrayList<>();
        File[] fs = errLog.listFiles(filter);
        if(null != fs && fs.length > 0){
            Collections.addAll(files, fs);
            Collections.sort(files, comparator);
            final List<FileEntry> fileEntries = new ArrayList<>(files.size());
            for(File f:files){
                FileEntry fileEntry = new FileEntry();
                fileEntry.file = f;
                fileEntry.date = f.getName().replace("crash_", "").replace(".txt", "");
                fileEntries.add(fileEntry);
            }
            setData(fileEntries);

            if(fileEntries.isEmpty()){
                return;
            }

            new Thread(){
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            XLogCrashAdapter.this.notifyDataSetInvalidated();
                        }
                    };

                    Pattern pattern = Pattern.compile("\\.([\\w]+?)(:|$)");

                    for(FileEntry fileEntry:fileEntries){
                        try {
                            FileReader fr = new FileReader(fileEntry.file);
                            BufferedReader br = new BufferedReader(fr);
                            String topLine = br.readLine();
                            Matcher matcher = pattern.matcher(topLine);
                            if(matcher.find()){
                                fileEntry.brief = matcher.group(1);
                            }

                            if(!TextUtils.isEmpty(fileEntry.brief)){
                                handler.sendEmptyMessage(0);
                            }

                            br.close();
                            fr.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CrashHolder crashHolder;
        if(convertView == null){
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(12f);
            tv.setClickable(false);
            tv.setGravity(Gravity.LEFT);
            int padding = XLogUtils.dp2px(getContext(), 10);
            tv.setPadding(padding, padding, padding, padding);
            tv.setTextColor(ColorPool.e_color);
            tv.setSingleLine(true);
            tv.setClickable(false);
            crashHolder = new CrashHolder(tv);
            tv.setTag(crashHolder);
        }else{
            crashHolder = (CrashHolder) convertView.getTag();
        }

        crashHolder.bind(position);
        return crashHolder.tv;
    }


    private class CrashHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView tv;
        int position;
        CrashHolder(TextView tv) {
            this.tv = tv;
            tv.setOnClickListener(this);
            tv.setOnLongClickListener(this);
        }

        void bind(int position){
            this.position = position;
            FileEntry fileEntry = getItem(position);
            if(!TextUtils.isEmpty(fileEntry.brief)){
                tv.setText(String.format("%s  [%s]",  fileEntry.date, fileEntry.brief));
            }else{
                tv.setText(fileEntry.file.getName());
            }
        }

        @Override
        public void onClick(View v) {
            if(null != panelContainer){
                String crashLog = getErrStr(position);
                panelContainer.showPanel(new TextPanel(crashLog, 0xffff2200));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            String crashLog = getErrStr(position);
            XLogUtils.sendText(getContext(), crashLog);
            return true;
        }
    }


    private final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith("crash_");
        }
    };

    private final Comparator<File> comparator = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return (int) (rhs.lastModified() - lhs.lastModified());
        }
    };


    private String getErrStr(int pos){
        try{
            FileEntry fileEntry = getItem(pos);
            FileReader fr = new FileReader(fileEntry.file);
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int ret;
            while(-1 != (ret = fr.read(buffer))){
                String s = new String(buffer, 0, ret);
                builder.append(s);
            }
            fr.close();
            return builder.toString();
        }catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }


    class FileEntry{
        File file;
        String brief = null;
        String date = null;
    }
}
