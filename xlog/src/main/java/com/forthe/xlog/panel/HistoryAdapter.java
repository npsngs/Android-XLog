package com.forthe.xlog.panel;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.Adapter;
import com.forthe.xlog.frame.ColorPool;
import com.forthe.xlog.tools.XLogUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


class HistoryAdapter extends Adapter<HistoryAdapter.FileEntry> {
    private Container container;
    private String historyDir;
    HistoryAdapter(Context mContext, Container container, String dir) {
        super(mContext);
        this.container = container;
        this.historyDir = dir;
    }

    void loadData() {
        File errLog = new File(historyDir);
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
                fileEntry.brief = f.getName();
                fileEntries.add(fileEntry);
            }

            if(fileEntries.isEmpty()){
                return;
            }

            setData(fileEntries);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(12f);
            tv.setClickable(false);
            tv.setGravity(Gravity.LEFT);
            int padding = XLogUtils.dp2px(getContext(), 10);
            tv.setPadding(padding, padding, padding, padding);
            tv.setTextColor(ColorPool.d_color);
            tv.setSingleLine(true);
            tv.setClickable(false);
            holder = new Holder(tv);
            tv.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.bind(position);
        return holder.tv;
    }


    private class Holder implements View.OnClickListener, View.OnLongClickListener{
        TextView tv;
        int position;
        Holder(TextView tv) {
            this.tv = tv;
            tv.setOnClickListener(this);
            tv.setOnLongClickListener(this);
        }

        void bind(int position){
            this.position = position;
            FileEntry fileEntry = getItem(position);
            if(!TextUtils.isEmpty(fileEntry.brief)){
                tv.setText(String.format("%s", fileEntry.brief));
            }else{
                tv.setText(fileEntry.file.getName());
            }
        }

        @Override
        public void onClick(View v) {
            if(null != container){
                FileEntry fileEntry = getItem(position);
                container.showPanel(new LocalLogPanel(fileEntry.file.getAbsolutePath()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            String crashLog = getFileStr(position);
            XLogUtils.sendText(getContext(), crashLog);
            return true;
        }
    }


    private final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith("log_");
        }
    };

    private final Comparator<File> comparator = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return (int) (rhs.lastModified() - lhs.lastModified());
        }
    };


    private String getFileStr(int pos){
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
    }
}
