package com.npsngs.debug;

import android.view.View;
import android.view.ViewGroup;

abstract class PanelBase {
    private View contentView;
    private ViewGroup parent;
    protected abstract View createView(ViewGroup parent);
    void attachTo(ViewGroup parent){
        this.parent = parent;
        contentView = createView(parent);
        parent.addView(contentView);
    }

    void dismiss(){
        if(isShow()){
            onDismiss();
            parent.removeView(contentView);
            parent = null;
            contentView = null;
        }
    }

    protected void onDismiss(){

    }

    boolean isShow(){
        return parent != null && contentView != null;
    }
}
