package com.npsngs.debug;

import android.view.View;
import android.view.ViewGroup;

abstract class PanelBase {
    private View contentView;
    private PanelContainer container;
    protected abstract View onCreateView(ViewGroup parent);
    View getContentView(ViewGroup parent){
        if(contentView == null){
            contentView = onCreateView(parent);
        }
        return contentView;
    }

    void onAttach(PanelContainer container){
        this.container = container;
    }

    void onDismiss(){
        contentView = null;
    }

    boolean isShow(){
        return contentView != null;
    }

    public void showPanel(PanelBase panel) {
        if(null != container){
            container.showPanel(panel);
        }
    }

    protected void dismiss(){
        if(null != container && isShow()){
            container.dismissPanel(this);
        }
    }
}
