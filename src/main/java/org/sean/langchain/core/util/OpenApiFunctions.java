package org.sean.langchain.core.util;

import dev.ai4j.openai4j.chat.Role;
import dev.ai4j.openai4j.shared.Usage;
import org.sean.langchain.core.data.message.AiMessage;
import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.data.message.SystemMessage;
import org.sean.langchain.core.data.message.ToolExecutionResultMessage;
import org.sean.langchain.core.data.output.TokenUsage;

import static dev.ai4j.openai4j.chat.Role.*;

public class OpenApiFunctions {

    public static Role roleFrom(ChatMessage message) {
        if (message instanceof AiMessage) {
            return ASSISTANT;
        } else if (message instanceof ToolExecutionResultMessage) {
            return FUNCTION;
        } else if (message instanceof SystemMessage) {
            return SYSTEM;
        } else {
            return USER;
        }
    }

    public static TokenUsage tokenUsageFrom(Usage openAiUsage) {
        if (openAiUsage == null) {
            return null;
        }
        return new TokenUsage(
                openAiUsage.promptTokens(),
                openAiUsage.completionTokens(),
                openAiUsage.totalTokens()
        );
    }


}
