package com.forthe.xlog.panel;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.frame.PanelBase;
import com.forthe.xlog.frame.ParseEngine;
import com.forthe.xlog.parser.JsonParser;
import com.forthe.xlog.parser.URLParser;
import com.forthe.xlog.span.JsonSpanCreator;
import com.forthe.xlog.span.URLSpanCreator;


public class TextPanel extends PanelBase {
    private int color;
    private String text;
    public TextPanel(String text, int color) {
        this.color = color;
        this.text = text;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.forthe_xlog_text_panel, parent, false);
        final TextView tv_message = (TextView) v.findViewById(R.id.tv_message_detail);
        tv_message.setTextColor(color);
        tv_message.setMovementMethod(LinkMovementMethod.getInstance());
        ParseEngine parseEngine = new ParseEngine();
        parseEngine.addParser(new JsonParser(), new JsonSpanCreator() {
            @Override
            protected void onJsonClick(String json) {
                showPanel(new JsonPanel(json));
            }
        });

        parseEngine.addParser(new URLParser(), new URLSpanCreator(){
            @Override
            protected void gotoHttpPanel(String url) {
                showPanel(new HttpPanel(url));
            }
        });

        tv_message.setText(text);

        try {
            parseEngine.startParse(text, new ParseEngine.OnParseCallback() {
                @Override
                public void onParseUpdate(String source, SpannableStringBuilder stringBuilder) {
                    tv_message.setText(stringBuilder);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}
