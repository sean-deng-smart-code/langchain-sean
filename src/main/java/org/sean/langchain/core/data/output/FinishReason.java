package org.sean.langchain.core.data.output;

public enum FinishReason {

    STOP,
    LENGTH,
    TOOL_EXECUTION,
    CONTENT_FILTER
}
