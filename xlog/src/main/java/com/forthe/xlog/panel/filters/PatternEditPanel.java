package com.forthe.xlog.panel.filters;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.PanelBase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public abstract class PatternEditPanel extends PanelBase implements TextView.OnEditorActionListener, TextWatcher {
    private EditText editText;
    private CheckBox cb_ignore_case;
    private RecyclerView rcv_history;
    private String findRegex;
    private Pattern findPattern;
    private FilterContainer filterContainer;
    private PatternHistoryDB historyDB;
    private HistoryAdapter adapter;
    private ExecutorService executor;
    private List<String> mostLastHistory;
    PatternEditPanel(int mode,FilterContainer filterContainer) {
        super(mode);
        this.filterContainer = filterContainer;
    }

    @Override
    protected void onResume(Container container) {
        super.onResume(container);
        isCompiled = false;
    }

    @Override
    protected void onAttach(Container container) {
        super.onAttach(container);
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                mostLastHistory = historyDB.getMostRecentlyUsed(12);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setHistories(mostLastHistory);
                    }
                });
            }
        });
    }

    @Override
    protected void onDetach(Container container) {
        super.onDetach(container);
        if(executor != null){
            executor.shutdown();
            executor = null;
        }
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {

        View root = View.inflate(context, R.layout.forthe_xlog_pattern_editor,null);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.RIGHT_OF, -1024);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, -1024);
        lp.addRule(RelativeLayout.ALIGN_TOP, -1024);
        root.setLayoutParams(lp);

        findRegex = filterContainer.getPattern();
        editText = (EditText) root.findViewById(R.id.et);
        editText.setOnEditorActionListener(this);
        editText.addTextChangedListener(this);
        if (!TextUtils.isEmpty(findRegex)) {
            editText.setText(findRegex);
        }

        cb_ignore_case = (CheckBox) root.findViewById(R.id.cb_ignore_case);
        cb_ignore_case.setChecked(filterContainer.isIgnoreCase());
        cb_ignore_case.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterContainer.setIgnoreCase(isChecked);
                isCompiled = false;
            }
        });

        rcv_history = (RecyclerView) root.findViewById(R.id.rcv_history);
        rcv_history.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.HORIZONTAL));
        final int itemPadding = (int) (context.getResources().getDisplayMetrics().density*5);
        rcv_history.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0,0,itemPadding,itemPadding);
            }
        });
        adapter = new HistoryAdapter();
        rcv_history.setAdapter(adapter);

        historyDB = new PatternHistoryDB(context);


        return root;
    }

    protected abstract void onFilterAction(String patternStr, Pattern pattern);

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(EditorInfo.IME_ACTION_SEARCH == actionId){
            if(compile()){
                dismiss();
            }
            return true;
        }
        return false;
    }

    public boolean compile(){
        CharSequence s = editText.getText();
        if(TextUtils.isEmpty(s)){
            findRegex = null;
            findPattern = null;
        }else{
            findRegex = s.toString();
            try{
                findPattern = Pattern.compile(String.format("%s%s",filterContainer.isIgnoreCase()?"(?i)":"(?-i)",s));
            }catch (Exception e){
                findPattern = null;
            }
        }
        isCompiled = true;
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        if(findRegex != null && executor != null && !executor.isShutdown()){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    historyDB.recordUsePattern(findRegex);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onFilterAction(findRegex, findPattern);
                        }
                    });
                }
            });
        }
        return null != findPattern;
    }


    private boolean isCompiled = false;
    public boolean isCompiled() {
        return isCompiled;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, int start, int before, int count) {
        isCompiled = false;
        if(null == executor || null == adapter){
            return;
        }

        if(isLastUseHistory){
            isLastUseHistory = false;
            return;
        }

        if(TextUtils.isEmpty(s)){
            adapter.setHistories(mostLastHistory);
        }else{
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    final List<String> ret = historyDB.searchByPrefix(s.toString(), 10);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setHistories(ret);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean isLastUseHistory = false;
    private class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv;
        public HistoryHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
            tv.setOnClickListener(this);
        }

        public void setBind(String history){
            tv.setText(history);
        }

        @Override
        public void onClick(View v) {
            isLastUseHistory = true;
            editText.setText(tv.getText());
        }
    }


    class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {
        private List<String> histories;
        public void setHistories(List<String> histories) {
            this.histories = histories;
            notifyDataSetChanged();
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = View.inflate(parent.getContext(),R.layout.forthe_xlog_pattern_item,null);
            return new HistoryHolder(v);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            String history = histories.get(position);
            holder.setBind(history);
        }

        @Override
        public int getItemCount() {
            return null==histories?0:histories.size();
        }
    }
}
