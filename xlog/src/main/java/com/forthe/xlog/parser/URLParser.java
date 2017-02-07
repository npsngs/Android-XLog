package com.forthe.xlog.parser;

import android.util.SparseIntArray;
import com.forthe.xlog.core.LogParser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParser implements LogParser {
    private Pattern pattern;
    public URLParser() {
        pattern = Pattern.compile("([\\s\\S]*?)(http:[^\\s^\\]^,'\"]+)[\\s\\S]*?");
    }

    @Override
    public SparseIntArray parse(String log) {
        Matcher matcher = pattern.matcher(log);
        SparseIntArray result = null;
        while (matcher.find()) {
            if(null == result) {
                result = new SparseIntArray();
            }

            int start = matcher.start(2);
            int end = matcher.end(2);
            result.append(start, end);
        }
        return result;
    }

}
