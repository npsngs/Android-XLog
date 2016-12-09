package com.npsngs.debug;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JsonParser implements TextParser {
    private Pattern pattern;
    JsonParser() {
        pattern = Pattern.compile("\\{[\\s\\S]+\\}");
    }

    @Override
    public void parse(SpannableStringBuilder spannableBuilder, String inputStr) {
        Matcher matcher = pattern.matcher(inputStr);
        while (matcher.find()){
            int startPosition = matcher.start(0);
            String json = matcher.group(0);
            spannableBuilder.setSpan(new ForegroundColorSpan(0xff009900), startPosition, startPosition+json.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }
}
