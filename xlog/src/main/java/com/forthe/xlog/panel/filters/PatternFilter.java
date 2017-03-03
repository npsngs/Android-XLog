package com.forthe.xlog.panel.filters;

import android.text.TextUtils;

import com.forthe.xlog.core.ItemFilter;

import java.util.regex.Pattern;


public class PatternFilter implements ItemFilter<String> {
    private Pattern findPattern = null;
    public void setFindPattern(Pattern findPattern) {
        this.findPattern = findPattern;
    }

    @Override
    public boolean filter(String item) {
        if(TextUtils.isEmpty(item)){
            return true;
        }
        return !(findPattern == null || findPattern.matcher(item).find());
    }
}
