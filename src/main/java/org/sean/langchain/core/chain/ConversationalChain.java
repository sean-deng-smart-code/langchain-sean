package org.sean.langchain.core.chain;

import lombok.Builder;
import org.sean.langchain.core.data.message.AiMessage;
import org.sean.langchain.core.data.message.UserMessage;
import org.sean.langchain.core.memory.ChatMemory;
import org.sean.langchain.core.memory.MessageWindowChatMemory;
import org.sean.langchain.core.mode.chat.ChatLanguageModel;

import static org.sean.langchain.core.util.ValidationUtils.ensureNotBlank;
import static org.sean.langchain.core.util.ValidationUtils.ensureNotNull;

public class ConversationalChain implements Chain<String,String>{

    private ChatLanguageModel chatLanguageModel;
    private final ChatMemory chatMemory;

    @Builder
    public ConversationalChain(ChatLanguageModel chatLanguageModel, ChatMemory chatMemory) {
        this.chatLanguageModel = ensureNotNull(chatLanguageModel, "chatLanguageModel");
        this.chatMemory = chatMemory == null ? MessageWindowChatMemory.builder().maxMessages(10).build() : chatMemory;
    }

    @Override
    public String execute(String message) {
        chatMemory.add(UserMessage.from(ensureNotBlank(message,"userMessage")));
        AiMessage result = chatLanguageModel.generate(chatMemory.messages()).content();
        chatMemory.add(result);
        return result.text();

    }
}
