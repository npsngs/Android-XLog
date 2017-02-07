package com.forthe.xlog.core;

public interface SpanCreator {
    Object createSpan(String source, int startPosition, int endPosition);
}
