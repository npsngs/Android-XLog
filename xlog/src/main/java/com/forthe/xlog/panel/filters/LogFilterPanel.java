package com.forthe.xlog.panel.filters;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.core.Panel;
import com.forthe.xlog.tools.XLogUtils;
import com.forthe.xlog.frame.Adapter;
import com.forthe.xlog.frame.PanelBase;
import com.forthe.xlog.view.LogAdapter;

import java.util.Arrays;
import java.util.regex.Pattern;

public class LogFilterPanel extends PanelBase implements AdapterView.OnItemClickListener{
    private LogAdapter logAdapter;
    private FilterContainer filterContainer;
    private MyAdapter adapter;
    public LogFilterPanel(int mode, FilterContainer filterContainer, LogAdapter logAdapter) {
        super(mode);
        this.filterContainer = filterContainer;
        this.logAdapter = logAdapter;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        context = parent.getContext();
        int w = XLogUtils.dp2px(context, 80);
        int h = RelativeLayout.LayoutParams.WRAP_CONTENT;
        ListView lv = new ListView(parent.getContext());
        lv.setBackgroundColor(0x55000000);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(XLogUtils.dp2px(context, 10), XLogUtils.dp2px(context, 50), 0,0);
        lv.setLayoutParams(lp);
        adapter = new MyAdapter(context);
        adapter.setData(Arrays.asList(filterContainer.getItems()));
        lv.setAdapter(adapter);
        lv.setBackgroundResource(R.drawable.sp_forthe_xlog_filters_bg);
        int padding = XLogUtils.dp2px(context, 5);
        lv.setPadding(padding,padding,padding,padding);
        lv.setOnItemClickListener(this);
        lv.setId(-1024);
        return lv;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FilterContainer.FilterItem item = adapter.getItem(position);
        String title = item.getTitle();
        if(item.isTagFilter()){
            if(item.isON()){
                logAdapter.removeFilterTag(title);
            }else{
                logAdapter.addFilterTag(item.getTitle());
            }
            filterContainer.switchItem(title);
        } else {
            if(item.isON()){
                filterContainer.switchItem(title);
                logAdapter.removeFilterPattern();
            }else{
                if(editPanel == null){
                    editPanel = new PatternEditPanel(Panel.MODE_FRIENDLY, filterContainer) {
                        @Override
                        protected void onFilterAction(String patternStr, Pattern pattern) {
                            FilterContainer.FilterItem item = adapter.getItem(4);
                            filterContainer.setPattern(patternStr);
                            if(item.isON()){
                                logAdapter.setFilterPattern(pattern);
                            }else{
                                filterContainer.switchItem(item.getTitle());
                                logAdapter.setFilterPattern(pattern);
                                adapter.notifyDataSetInvalidated();
                            }
                        }
                    };
                }

                if(!editPanel.isShow()){
                    showPanel(editPanel);
                }else{
                    if(!editPanel.isCompiled()){
                        editPanel.compile();
                    }
                    editPanel.dismiss();
                }
            }
        }

        adapter.notifyDataSetInvalidated();
    }

    private PatternEditPanel editPanel;
    class MyAdapter extends Adapter<FilterContainer.FilterItem> {

        MyAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            TextView tv;
            if(v == null){
                tv = new TextView(getContext());
                tv.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
                tv.setTextSize(14f);
                tv.setTextColor(0xffffffff);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setClickable(false);
                tv.setGravity(Gravity.CENTER);
                int padding = XLogUtils.dp2px(getContext(), 10);
                tv.setPadding(padding, padding, padding, padding);
            }else{
                tv = (TextView) v;
            }

            FilterContainer.FilterItem item = getItem(position);
            tv.setText(item.getTitle());
            tv.setBackgroundColor(item.isON()?0x550088ff:0x00000000);

            return tv;
        }
    }
}
