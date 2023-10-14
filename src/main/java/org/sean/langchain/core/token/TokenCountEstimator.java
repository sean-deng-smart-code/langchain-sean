package org.sean.langchain.core.token;

import org.sean.langchain.core.data.segment.TextSegment;

import java.util.List;

/**
 * Represents an interface for estimating the count of tokens in various texts, text segments, etc.
 * This can be useful when it's necessary to know in advance the cost of processing a specified text by the LLM.
 */
public interface TokenCountEstimator {

    int estimateTokenCount(String text);

    default int estimateTokenCount(TextSegment textSegment) {
        return estimateTokenCount(textSegment.text());
    }

    default int estimateTokenCount(List<TextSegment> textSegments) {
        int tokenCount = 0;
        for (TextSegment textSegment : textSegments) {
            tokenCount += estimateTokenCount(textSegment);
        }
        return tokenCount;
    }
}