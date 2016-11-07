package com.npsngs.debug;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


class DEBugCrashAdapter extends Adapter<File> {
    DEBugCrashAdapter(Context mContext) {
        super(mContext);
    }

    public void loadData() {
        File errLog = new File(DEBug.getLogSaveDir());
        if (!errLog.exists() || !errLog.isDirectory()) {
            return;
        }

        List<File> files = new ArrayList<>();
        File[] fs = errLog.listFiles(filter);
        if(null != fs && fs.length > 0){
            Collections.addAll(files, fs);
            Collections.sort(files, comparator);
            setData(files);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CrashHolder crashHolder;
        if(v == null){
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(12f);
            tv.setClickable(false);
            tv.setGravity(Gravity.LEFT);
            int padding = DEBugUtils.dp2px(getContext(), 10);
            tv.setPadding(padding, padding, padding, padding);
            tv.setTextColor(0xffff2200);
            tv.setSingleLine(false);
            tv.setClickable(false);
            crashHolder = new CrashHolder(tv);
            tv.setTag(crashHolder);
        }else{
            crashHolder = (CrashHolder) v.getTag();
        }

        crashHolder.bind(position);
        return crashHolder.tv;
    }


    class CrashHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView tv;
        int position;
        public CrashHolder(TextView tv) {
            this.tv = tv;
            tv.setOnClickListener(this);
            tv.setOnLongClickListener(this);
        }

        void bind(int position){
            this.position = position;
            File file = getItem(position);
            tv.setText(file.getName());
        }

        @Override
        public void onClick(View v) {
            if(null != onShowParseText){
                String crashLog = getErrStr(position);
                onShowParseText.showParsedText(crashLog, 0xffff2200);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            String crashLog = getErrStr(position);
            DEBugUtils.sendText(getContext(), crashLog);
            return true;
        }
    }


    private final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith("errlog");
        }
    };

    private final Comparator<File> comparator = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            return (int) (rhs.lastModified() - lhs.lastModified());
        }
    };

    private DEBugPopup.OnShowParseText onShowParseText;
    public void setOnShowParseText(DEBugPopup.OnShowParseText onShowParseText) {
        this.onShowParseText = onShowParseText;
    }

    private String getErrStr(int pos){
        try{
            File file = getItem(pos);
            FileReader fr = new FileReader(file);
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

}
