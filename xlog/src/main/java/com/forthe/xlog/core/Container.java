package com.forthe.xlog.core;

import android.content.Context;
import android.view.ViewGroup;

public interface Container {
    Context getContext();

    ViewGroup getContainer();

    void showPanel(Panel child);

    /**
     * @return if true ,dismiss child success
     */
    boolean dismissPanel(Panel child);

    /**
     * @return if true ,dismiss child of the top one success
     */
    boolean dismissChild();

    /**
     * @return if true ,dismiss children success
     */
    boolean dismissAllChild();

    boolean isEmpty();
}
