package com.forthe.xlog;
import android.text.SpannableStringBuilder;

interface TextParser {
    void parse(SpannableStringBuilder spannableBuilder, String inputStr);
}
