package com.forthe.xlog.core;

import android.view.View;

public interface Panel{
    int MODE_EXCLUSIVE = 1;
    int MODE_FRIENDLY = 2;

    int STATUS_ATTACH = 1;
    int STATUS_RESUME = 2;
    int STATUS_PAUSE = 3;
    int STATUS_DETACH = 4;

    int getMode();
    int getStatus();
    View getView(Container container);
    void attach(Container container);
    void detach(Container container);
    void resume(Container container);
    void pause(Container container);
    void dismiss();
    void showPanel(Panel panel);
    boolean isShow();
}
