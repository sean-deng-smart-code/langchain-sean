package org.sean.langchain.core.mode.chat;


import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.data.message.UserMessage;
import org.sean.langchain.core.data.prompt.Prompt;
import org.sean.langchain.core.data.segment.TextSegment;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.sean.langchain.core.data.message.UserMessage.userMessage;

/**
 * Represents an interface for estimating the count of tokens in various text types such as a text, message, prompt, text segment, etc.
 * This can be useful when it's necessary to know in advance the cost of processing a specified text by the LLM.
 */
public interface TokenCountEstimator {

    default int estimateTokenCount(String text) {
        return estimateTokenCount(userMessage(text));
    }

    default int estimateTokenCount(UserMessage userMessage) {
        return estimateTokenCount(singletonList(userMessage));
    }

    default int estimateTokenCount(Prompt prompt) {
        return estimateTokenCount(prompt.text());
    }

    default int estimateTokenCount(TextSegment textSegment) {
        return estimateTokenCount(textSegment.text());
    }

    int estimateTokenCount(List<ChatMessage> messages);
}
