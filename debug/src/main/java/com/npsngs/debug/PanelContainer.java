package com.npsngs.debug;

import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

class PanelContainer {
    private Stack<PanelBase> panelStack;
    private ViewGroup container;

    public PanelContainer(ViewGroup container) {
        this.container = container;
    }

    public void showPanel(PanelBase panel){
        if(panelStack == null){
            panelStack = new Stack<>();
        }

        container.setVisibility(View.VISIBLE);
        View v = panel.getContentView(container);
        container.addView(v);
        panelStack.add(panel);
        panel.onAttach(this);
    }

    public boolean dismissPanel(){
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        PanelBase panel = panelStack.pop();
        if(null != panel){
            container.removeView(panel.getContentView(container));
            panel.onDismiss();
            if(panelStack.isEmpty()){
                container.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return false;
    }

    public boolean dismissPanel(PanelBase panel){
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        if(panelStack.remove(panel)){
            container.removeView(panel.getContentView(container));
            panel.onDismiss();
            if(panelStack.isEmpty()){
                container.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return false;
    }


    public boolean dismissAllPanel(){
        if(panelStack == null || panelStack.isEmpty()){
            container.setVisibility(View.INVISIBLE);
            return false;
        }

        while (!panelStack.isEmpty()){
            PanelBase panel = panelStack.pop();
            if(null != panel){
                container.removeView(panel.getContentView(container));
                panel.onDismiss();
            }
        }

        container.setVisibility(View.INVISIBLE);
        return true;
    }
}
