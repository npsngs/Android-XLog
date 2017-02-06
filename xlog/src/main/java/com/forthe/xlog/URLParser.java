package com.forthe.xlog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class URLParser implements TextParser {
    private Pattern pattern;
    URLParser() {
        pattern = Pattern.compile("([\\s\\S]*?)(http:[^\\s^\\]^,'\"]+)[\\s\\S]*?");
    }
    @Override
    public void parse(SpannableStringBuilder spannableBuilder, String inputStr) {
        Matcher matcher = pattern.matcher(inputStr);
        while (matcher.find()){
            int startPosition = matcher.start(2);
            String  url = matcher.group(2);
            spannableBuilder.setSpan(new UrlClickableSpan(url), startPosition, startPosition+url.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    class UrlClickableSpan extends ClickableSpan {
        private String url;
        UrlClickableSpan(String url) {
            super();
            this.url = url;
        }


        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(0xff3388cc);
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
