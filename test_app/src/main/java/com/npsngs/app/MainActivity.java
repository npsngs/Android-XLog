package com.npsngs.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.forthe.xlog.frame.Adapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private PageAdater adater;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = (ListView) findViewById(R.id.lv_list);
        adater = new PageAdater(this);
        lv.setAdapter(adater);
    }

    class PageAdater extends Adapter<Page> {

        public PageAdater(Context mContext) {
            super(mContext);
            List<Page> pages = new ArrayList<>();
            pages.add(new Page("XLog", XlogActivity.class));
            pages.add(new Page("DownloadCore", DownloadCoreActivity.class));
            setData(pages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Holder holder;
            if(v == null){
                TextView tv = new TextView(getContext());
                tv.setTextColor(0xff787878);
                tv.setTextSize(14f);
                tv.setPadding(10, 5, 10, 5);
                holder = new Holder(tv);
                tv.setTag(holder);
                v = tv;
            }else{
                holder = (Holder) v.getTag();
            }
            holder.bindData(position);
            return v;
        }
    }

    class Holder implements View.OnClickListener{
        TextView tv;
        int pos;
        public Holder(TextView tv) {
            this.tv = tv;
            tv.setOnClickListener(this);
        }

        public void bindData(int pos){
            this.pos = pos;
            Page page = adater.getItem(pos);
            tv.setText(page.title);
        }

        @Override
        public void onClick(View v) {
            Page page = adater.getItem(pos);
            Intent intent = new Intent(MainActivity.this, page.pageCls);
            startActivity(intent);
        }
    }

    class Page{
        String title;
        Class<? extends  Activity> pageCls;
        public Page(String title, Class<? extends Activity> pageCls) {
            this.title = title;
            this.pageCls = pageCls;
        }
    }
}
