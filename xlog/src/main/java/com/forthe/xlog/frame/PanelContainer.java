package com.forthe.xlog.frame;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.forthe.xlog.core.Panel;
import com.forthe.xlog.core.Container;

import java.util.Stack;


public class PanelContainer implements Container {
    private Stack<Panel> panelStack;
    private ViewGroup container;
    private Context context;
    public PanelContainer(ViewGroup container) {
        this.container = container;
        this.context = container.getContext();
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(null != panelStack && !panelStack.isEmpty()){
                   Panel panel = panelStack.peek();
                    if(panel.getMode() == Panel.MODE_FRIENDLY){
                        return false;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public ViewGroup getContainer() {
        return container;
    }

    @Override
    public void showPanel(Panel child) {
        if(panelStack == null){
            panelStack = new Stack<>();
        }

        View v = child.getView(this);
        if(v == null){
            return;
        }

        if(child.getMode() == Panel.MODE_EXCLUSIVE && !panelStack.isEmpty()){
            Panel pre = panelStack.peek();
            if(null != pre){
                container.removeView(pre.getView(this));
                pre.pause(this);
            }
        }

        panelStack.add(child);
        child.attach(this);

        container.setVisibility(View.VISIBLE);
        container.addView(v);
        child.resume(this);
    }

    @Override
    public boolean dismissPanel(Panel child) {
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        if(!panelStack.contains(child)){
            return false;
        }

        boolean isNeedResume = false;
        if(child.getMode() == Panel.MODE_EXCLUSIVE && child.equals(panelStack.peek())){
            isNeedResume = true;
        }

        container.removeView(child.getView(this));
        child.pause(this);
        panelStack.remove(child);
        child.detach(this);

        if(panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
        }else if(isNeedResume){
            Panel pre = panelStack.peek();
            container.addView(pre.getView(this));
            pre.resume(this);
        }

        return true;
    }

    @Override
    public boolean dismissChild() {
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        Panel panel = panelStack.peek();
        if(null != panel){
            container.removeView(panel.getView(this));
            panel.pause(this);
            panelStack.pop();
            panel.detach(this);

            if(panelStack.isEmpty()){
                container.setVisibility(View.INVISIBLE);
            }else if(panel.getMode() == Panel.MODE_EXCLUSIVE){
                Panel pre = panelStack.peek();
                container.addView(pre.getView(this));
                pre.resume(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean dismissAllChild() {
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        while (!panelStack.isEmpty()){
            Panel panel = panelStack.pop();
            if(null != panel){
                container.removeView(panel.getView(this));
                panel.pause(this);
                panel.detach(this);
            }
        }

        container.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public boolean isEmpty() {
        return panelStack == null || panelStack.isEmpty();
    }


}
