package com.npsngs.debug;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;


class TouchListView extends ListView {
    public TouchListView(Context context) {
        super(context);
    }

    public TouchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            if(null != downTouchListener){
                downTouchListener.onDownTouch();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private OnDownTouchListener downTouchListener;
    public void setDownTouchListener(OnDownTouchListener downTouchListener) {
        this.downTouchListener = downTouchListener;
    }
    interface OnDownTouchListener{
        void onDownTouch();
    }
}
