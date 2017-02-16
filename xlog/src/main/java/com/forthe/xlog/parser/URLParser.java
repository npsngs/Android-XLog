package com.forthe.xlog.parser;

import android.util.SparseIntArray;
import com.forthe.xlog.core.LogParser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParser implements LogParser {
    private Pattern pattern;
    public URLParser() {
        //((?!abc).)*  的意思就是匹配  不含abc的字符(?!abc) 跟 任意字符. 的组合，出现任何次*
        pattern = Pattern.compile("http://((?!,http|\\s|\\]|\\)|\\}|'|\").)+");
    }

    @Override
    public SparseIntArray parse(String log) {
        Matcher matcher = pattern.matcher(log);
        SparseIntArray result = null;
        while (matcher.find()) {
            if(null == result) {
                result = new SparseIntArray();
            }

            int start = matcher.start(0);
            int end = matcher.end(0);
            result.append(start, end);
        }
        return result;
    }

}
