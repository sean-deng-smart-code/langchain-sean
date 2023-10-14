package org.sean.langchain.core.mode.chat;

import org.sean.langchain.core.data.message.AiMessage;
import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.data.message.UserMessage;
import org.sean.langchain.core.data.output.StreamingResponseHandler;
import org.sean.langchain.core.tool.ToolSpecification;

import java.util.List;

import static java.util.Arrays.asList;

public interface StreamChatLanguageModel {

    default void generate(String message, StreamingResponseHandler<AiMessage> messageHandler){
         generate(messageHandler,UserMessage.from(message));
    }

    default void generate(StreamingResponseHandler<AiMessage> messageHandler,ChatMessage... messages) {
        generate(messageHandler,asList(messages));
    }

    void generate(StreamingResponseHandler<AiMessage> messageHandler,List<ChatMessage> messages);


    void generate(StreamingResponseHandler<AiMessage> messageHandler,List<ChatMessage> messages, List<ToolSpecification> tools);

    void generate(StreamingResponseHandler<AiMessage> messageHandler,List<ChatMessage> messages,ToolSpecification tool);
}
