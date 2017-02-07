package com.forthe.xlog.span;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import com.forthe.xlog.core.SpanCreator;
import com.forthe.xlog.frame.ColorPool;

public class URLSpanCreator implements SpanCreator {
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
            Uri uri = Uri.parse(url);
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
            }
        }
    }

}
