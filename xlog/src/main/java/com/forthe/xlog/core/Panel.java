package com.forthe.xlog.core;

import android.view.View;

public interface Panel{
    View getView(Container container);
    void onAttach(Container container);
    void onDetach(Container container);
    void dismiss();
    void showPanel(Panel panel);
    boolean isShow();
}
