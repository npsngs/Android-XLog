package com.forthe.xlog.frame;

import android.view.View;
import android.view.ViewGroup;

import com.forthe.xlog.core.Panel;
import com.forthe.xlog.core.Container;

import java.util.Stack;


public class PanelContainer implements Container {
    private Stack<Panel> panelStack;
    private ViewGroup container;

    public PanelContainer(ViewGroup container) {
        this.container = container;
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

        container.setVisibility(View.VISIBLE);
        View v = child.getView(this);
        container.addView(v);
        panelStack.add(child);
        child.onAttach(this);
    }

    @Override
    public boolean dismissPanel(Panel child) {
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        if(panelStack.remove(child)){
            container.removeView(child.getView(this));
            child.onDetach(this);
            if(panelStack.isEmpty()){
                container.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean dismissChild() {
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        Panel panel = panelStack.pop();
        if(null != panel){
            container.removeView(panel.getView(this));
            panel.onDetach(this);
            if(panelStack.isEmpty()){
                container.setVisibility(View.INVISIBLE);
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
                panel.onDetach(this);
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
