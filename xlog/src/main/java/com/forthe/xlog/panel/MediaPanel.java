package com.forthe.xlog.panel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forthe.xlog.R;
import com.forthe.xlog.core.Container;
import com.forthe.xlog.frame.PanelBase;


public class MediaPanel extends PanelBase {
    private String str;
    private Bitmap bitmap;
    public MediaPanel(String str,Bitmap bitmap) {
        this.str = str;
        this.bitmap = bitmap;
    }

    @Override
    protected View onCreateView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.forthe_xlog_media_panel, parent, false);
        TextView tv_message = (TextView) v.findViewById(R.id.tv_message_detail);
        tv_message.setTextColor(Color.WHITE);
        tv_message.setMovementMethod(LinkMovementMethod.getInstance());
        tv_message.setText(str);

        ImageView iv_image = (ImageView) v.findViewById(R.id.iv_image);
        iv_image.setImageBitmap(bitmap);
        return v;
    }

    @Override
    protected void onDetach(Container container) {
        super.onDetach(container);
        try{
            bitmap.recycle();
            bitmap = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
