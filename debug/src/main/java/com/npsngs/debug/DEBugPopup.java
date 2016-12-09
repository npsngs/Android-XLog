package com.npsngs.debug;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

class DEBugPopup implements View.OnClickListener {
    private Activity activity;
    private PopupWindow popupWindow;
    private TextView tv_config_list, tv_log_list, tv_error_list, tv_message_detail,
            tv_title_right, tv_title_center;
    private ListView lv;
    private HVScrollView hsv_message_detail;
    private DEBugConfigAdapter configAdapter;
    private DEBugLogAdapter logAdapter;
    private DEBugCrashAdapter crashAdapter;
    boolean isCurrentActivity(Activity activity){
        return activity != null && activity.equals(this.activity);
    }

    DEBugPopup(Activity activity) {
        this.activity = activity;
        View root = View.inflate(activity, R.layout.pop_debug_window, null);
        initView(root);
        popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));

    }

    void show(){
        show(DEBug.PAGE_CONFIG);

    }

    void show(int page){
        if(popupWindow.isShowing()){
            return;
        }

        View decorView = activity.getWindow().getDecorView();
        if(decorView.getWindowToken() == null){
            return;
        }

        popupWindow.showAtLocation(decorView, Gravity.TOP, 0, 0);

        switchPage(page);
    }

    private void initView(View root){
        configAdapter = new DEBugConfigAdapter(activity);
        logAdapter = new DEBugLogAdapter(activity);
        crashAdapter = new DEBugCrashAdapter(activity);

        lv = (ListView) root.findViewById(R.id.lv);
        tv_config_list = (TextView) root.findViewById(R.id.tv_config_list);
        tv_log_list = (TextView) root.findViewById(R.id.tv_log_list);
        tv_error_list = (TextView) root.findViewById(R.id.tv_error_list);
        tv_message_detail = (TextView) root.findViewById(R.id.tv_message_detail);
        hsv_message_detail = (HVScrollView) root.findViewById(R.id.hsv_message_detail);
        tv_message_detail.setMovementMethod(LinkMovementMethod.getInstance());


        tv_title_right = (TextView) root.findViewById(R.id.tv_title_right);
        tv_title_center = (TextView) root.findViewById(R.id.tv_title_center);

        root.findViewById(R.id.tv_back).setOnClickListener(this);
        tv_config_list.setOnClickListener(this);
        tv_log_list.setOnClickListener(this);
        tv_error_list.setOnClickListener(this);
        tv_title_right.setOnClickListener(this);

        initOnShowParseText();
        logAdapter.setOnShowParseText(onShowParseText);
        crashAdapter.setOnShowParseText(onShowParseText);
    }


    void dismiss(){
        if(popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.tv_back == id) {
            if (hsv_message_detail.getVisibility() == View.VISIBLE) {
                hsv_message_detail.setVisibility(View.GONE);
            } else {
                dismiss();
            }
        } else if(R.id.tv_title_right == id){
            if(currentPage == DEBug.PAGE_LOGS){
                DEBug.clearLog();
                logAdapter.clear();
            }
        } else if(R.id.tv_config_list == id){
            switchPage(DEBug.PAGE_CONFIG);
        } else if(R.id.tv_log_list == id){
            switchPage(DEBug.PAGE_LOGS);
        } else if(R.id.tv_error_list == id){
            switchPage(DEBug.PAGE_CRASH);
        }
    }

    /**
     * @param page
     * 0  switches
     * 1  Logs
     * 2  Error Logs
     */
    private int currentPage;
    private void switchPage(int page){
        currentPage = page;
        hsv_message_detail.setVisibility(View.GONE);

        tv_config_list.setSelected(false);
        tv_log_list.setSelected(false);
        tv_error_list.setSelected(false);
        switch (page){
            case DEBug.PAGE_CONFIG:
                tv_config_list.setSelected(true);
                tv_title_center.setText("DEBug");
                tv_title_right.setText("");
                lv.setAdapter(configAdapter);
                break;
            case DEBug.PAGE_LOGS:
                tv_log_list.setSelected(true);
                tv_title_center.setText("Log");
                tv_title_right.setText("clear");
                lv.setAdapter(logAdapter);
                break;
            case DEBug.PAGE_CRASH:
                tv_error_list.setSelected(true);
                tv_title_center.setText("Crash");
                tv_title_right.setText("");
                lv.setAdapter(crashAdapter);
                crashAdapter.loadData();
                break;
            default:
        }
    }



    private OnShowParseText onShowParseText;
    interface OnShowParseText{
        void showParsedText(String text, int color);
    }

    private TextParseEngine textParseEngine;
    private void initOnShowParseText(){
        onShowParseText = new OnShowParseText() {
            @Override
            public void showParsedText(String text, int color) {
                if(null == textParseEngine){
                    initParseEngine();
                }
                hsv_message_detail.setVisibility(View.VISIBLE);
                hsv_message_detail.scrollTo(0,0);
                tv_message_detail.setTextColor(color);

                textParseEngine.startParse(text);
            }
        };
    }

    private void initParseEngine(){
        textParseEngine = new TextParseEngine() {
            @Override
            void onUpdateParseResult(SpannableStringBuilder spannableBuilder) {
                tv_message_detail.setText(spannableBuilder);
            }
        };
        textParseEngine.add(new URLParser());
        textParseEngine.add(new JsonParser());
    }
}
