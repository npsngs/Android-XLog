package com.npsngs.debug;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;

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
            spannableBuilder.setSpan(new URLSpan(url), startPosition, startPosition+url.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }
}
