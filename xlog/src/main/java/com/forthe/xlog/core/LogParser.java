package com.forthe.xlog.core;

import android.util.SparseIntArray;

public interface LogParser {
    /**
     * @return key is the start position of one match,value is the match's end position
     */
    SparseIntArray parse(String log);
}
