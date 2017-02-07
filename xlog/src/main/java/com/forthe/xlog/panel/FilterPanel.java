package com.forthe.xlog.panel;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.tools.XLogUtils;
import com.forthe.xlog.core.ItemFilter;
import com.forthe.xlog.frame.Adapter;
import com.forthe.xlog.frame.FilterAdapter;
import com.forthe.xlog.frame.PanelBase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FilterPanel extends PanelBase implements AdapterView.OnItemClickListener{
    private FilterAdapter<String> filterAdapter;
    private List<MYFilter> filters;
    private MyAdapter adapter;
    public FilterPanel(FilterAdapter<String> filterAdapter) {
        this.filterAdapter = filterAdapter;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        createFilters();
        Context context = parent.getContext();
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
        adapter.setData(filters);
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
        MYFilter filter = adapter.getItem(position);
        if(filter.isUsed()){
            filter.setUsed(false);
            filterAdapter.removeItemFilter(filter);
        }else{
            if(3 == position){
                if(editPanel == null){
                    editPanel = new EditPanel();
                }

                if(!editPanel.isShow()){
                    showPanel(editPanel);
                }else{
                    if(!editPanel.isCompiled){
                        editPanel.compile();
                    }
                    editPanel.dismiss();
                    filter.setUsed(true);
                    filterAdapter.addItemFilter(filter);
                }
            }else{
                filter.setUsed(true);
                filterAdapter.addItemFilter(filter);
            }
        }
        adapter.notifyDataSetInvalidated();
    }

    private EditPanel editPanel;
    class MyAdapter extends Adapter<MYFilter> {

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

            MYFilter filter = getItem(position);
            tv.setText(filter.title);
            tv.setBackgroundColor(filter.isUsed?0x550088ff:0x00000000);

            return tv;
        }
    }



    private Pattern findPattern;
    private void createFilters(){
        if(null != filters){
            return;
        }

        filters = new ArrayList<>();
        filters.add(new MYFilter("D") {
            @Override
            public boolean filter(String item) {
                if(TextUtils.isEmpty(item)){
                    return false;
                }
                return item.startsWith("D");
            }
        });

        filters.add(new MYFilter("W") {
            @Override
            public boolean filter(String item) {
                if(TextUtils.isEmpty(item)){
                    return false;
                }
                return item.startsWith("W");
            }
        });

        filters.add(new MYFilter("E") {
            @Override
            public boolean filter(String item) {
                if(TextUtils.isEmpty(item)){
                    return false;
                }
                return item.startsWith("E");
            }
        });

        filters.add(new MYFilter("find") {
            @Override
            public boolean filter(String item) {
                if(TextUtils.isEmpty(item)){
                    return true;
                }

                if(findPattern == null || findPattern.matcher(item).find()){
                    return false;
                }

                return true;
            }
        });
    }


    static abstract class MYFilter implements ItemFilter<String> {
        private String title;
        private boolean isUsed =false;
        MYFilter(String title) {
            this.title = title;
        }
        boolean isUsed() {
            return isUsed;
        }

        void setUsed(boolean used) {
            isUsed = used;
        }
    }



    private CharSequence findRegex;
    class EditPanel extends PanelBase implements TextView.OnEditorActionListener, TextWatcher{
        private EditText editText;
        @Override
        protected View onCreateView(ViewGroup parent) {
            Context context = parent.getContext();
            int w = RelativeLayout.LayoutParams.MATCH_PARENT;
            int h = RelativeLayout.LayoutParams.WRAP_CONTENT;
            editText = new EditText(context);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.addRule(RelativeLayout.RIGHT_OF, -1024);
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, -1024);
            int margin = XLogUtils.dp2px(context, 10);
            lp.setMargins(margin, 0, margin,0);
            editText.setGravity(Gravity.CENTER_VERTICAL);
            editText.setLayoutParams(lp);
            editText.setOnEditorActionListener(this);
            editText.addTextChangedListener(this);
            editText.setTextSize(14f);
            editText.setTextColor(0xffffffff);
            editText.setHintTextColor(0xffa3a3a3);
            editText.setHint("regex expression");
            editText.setBackgroundResource(R.drawable.sp_forthe_xlog_filters_bg);
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setSingleLine(true);
            int padding = XLogUtils.dp2px(context, 15);
            editText.setPadding(padding,margin,padding,padding);
            if(findRegex != null){
                editText.setText(findRegex);
            }
            return editText;
        }


        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(EditorInfo.IME_ACTION_SEARCH == actionId){
                compile();

                MYFilter filter = adapter.getItem(3);
                if(filter.isUsed()){
                    filterAdapter.onFilterChange();
                }else{
                    filter.setUsed(true);
                    filterAdapter.addItemFilter(filter);
                }
                adapter.notifyDataSetInvalidated();
                dismiss();
                return true;
            }
            return false;
        }

        public void compile(){
            CharSequence s = editText.getText();
            if(TextUtils.isEmpty(s)){
                findRegex = null;
                findPattern = null;
            }else{
                findRegex = s;
                try{
                    findPattern = Pattern.compile(String.format("%s",s));
                }catch (Exception e){
                    findPattern = null;
                }
            }
            isCompiled = true;

            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        }


        private boolean isCompiled = false;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isCompiled = false;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
