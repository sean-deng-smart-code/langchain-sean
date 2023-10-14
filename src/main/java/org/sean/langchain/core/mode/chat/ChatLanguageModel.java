package org.sean.langchain.core.mode.chat;

import org.sean.langchain.core.data.message.AiMessage;
import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.data.message.UserMessage;
import org.sean.langchain.core.data.output.Response;
import org.sean.langchain.core.tool.ToolSpecification;

import java.util.List;

import static java.util.Arrays.asList;

public interface ChatLanguageModel {

    /**
     * gennrate content by chat Language model
     * @param message
     * @return
     */
    default String generate(String message){
        return generate(UserMessage.from(message)).content().text();
    }

    default Response<AiMessage> generate(ChatMessage... messages) {
        return generate(asList(messages));
    }

    Response<AiMessage> generate(List<ChatMessage> messages);


    Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> tools);

    Response<AiMessage> generate(List<ChatMessage> messages,ToolSpecification tool);


}
