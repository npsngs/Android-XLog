package com.forthe.xlog.span;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import com.forthe.xlog.core.SpanCreator;
import com.forthe.xlog.frame.ColorPool;
import com.forthe.xlog.tools.XLogUtils;

public abstract class URLSpanCreator implements SpanCreator {

    @Override
    public Object createSpan(String source, int startPosition, int endPosition) {
        String  url = source.substring(startPosition, endPosition);
        return new UrlClickableSpan(url);
    }

    class UrlClickableSpan extends ClickableSpan {
        private String url;
        UrlClickableSpan(String url) {
            super();
            this.url = url;
        }


        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ColorPool.url_color);
            ds.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget) {
            PackageManager pm = widget.getContext().getPackageManager();
            boolean hasPermission = (PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission("android.permission.INTERNET", widget.getContext().getPackageName()));
            if (hasPermission) {
                gotoHttpPanel(url);
            }else {
                Context context = widget.getContext();
                XLogUtils.viewUrl(context, url);
            }
        }
    }

    protected abstract void gotoHttpPanel(String url);
}
