package com.forthe.xlog.frame;

import android.view.View;
import android.view.ViewGroup;

import com.forthe.xlog.core.Panel;
import com.forthe.xlog.core.Container;

public abstract class PanelBase implements Panel{
    private Container container;
    private View contentView;
    @Override
    public View getView(Container container) {
        if(null == contentView){
            ViewGroup parent = container.getContainer();
            if(parent != null){
                contentView = onCreateView(parent);
            }
        }
        return contentView;
    }

    @Override
    public void dismiss() {
        if(isShow()){
            container.dismissPanel(this);
        }
    }

    @Override
    public void onAttach(Container container) {
        this.container = container;
    }

    @Override
    public void onDetach(Container container) {
        this.container = null;
    }

    @Override
    public boolean isShow() {
        return container != null;
    }

    @Override
    public void showPanel(Panel panel) {
        if (isShow()){
            container.showPanel(panel);
        }
    }

    protected abstract View onCreateView(ViewGroup parent);
}
