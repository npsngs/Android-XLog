package com.forthe.xlog.panel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.forthe.xlog.R;
import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.PanelBase;

public class HistoryPanel extends PanelBase {
    private String dir;
    private ListView listView;
    public HistoryPanel(String dir) {
        this.dir = dir;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        listView = (ListView) inflater.inflate(R.layout.forthe_xlog_singlelist, parent, false);
        return listView;
    }

    @Override
    public void onAttach(Container container) {
        super.onAttach(container);
        HistoryAdapter adapter = new HistoryAdapter(container.getContainer().getContext(),container,dir);
        listView.setAdapter(adapter);
        adapter.loadData();
    }
}
