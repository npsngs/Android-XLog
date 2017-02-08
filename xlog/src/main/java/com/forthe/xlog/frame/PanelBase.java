package com.forthe.xlog.frame;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.forthe.xlog.core.Panel;
import com.forthe.xlog.core.Container;

public abstract class PanelBase implements Panel{
    private Container container;
    private View contentView;
    private int mode = MODE_EXCLUSIVE;
    private int status = STATUS_DETACH;

    public PanelBase() {
    }

    public PanelBase(int mode) {
        this.mode = mode;
    }

    @Override
    public View getView(Container container) {
        if(null == contentView){
            ViewGroup parent = container.getContainer();
            if(parent != null){
                contentView = onCreateView(container.getContext(), parent);
            }
        }
        return contentView;
    }

    @Override
    public final void attach(Container container) {
        this.container = container;
        status = STATUS_ATTACH;
        onAttach(container);
    }

    @Override
    public final void detach(Container container) {
        this.container = null;
        status = STATUS_DETACH;
        onDetach(container);
    }

    @Override
    public final void resume(Container container) {
        status = STATUS_RESUME;
        onResume(container);
    }

    @Override
    public final void pause(Container container) {
        status = STATUS_PAUSE;
        onPause(container);
    }

    @Override
    public final void dismiss() {
        if(isShow()){
            container.dismissPanel(this);
        }
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public boolean isShow() {
        return container != null;
    }

    @Override
    public final void showPanel(Panel panel) {
        if (isShow()){
            container.showPanel(panel);
        }
    }

    protected abstract View onCreateView(Context context, ViewGroup parent);

    protected void onAttach(Container container){

    }
    protected void onDetach(Container container){

    }
    protected void onResume(Container container){

    }
    protected void onPause(Container container){

    }
}
