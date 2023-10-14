package org.sean.langchain.core.constants;

import org.sean.langchain.core.data.message.ChatMessage;

import static org.sean.langchain.core.exception.Exceptions.illegalArgument;

public enum ChatMessageType {
    SYSTEM,
    USER,
    AI,
    TOOL_EXECUTION_RESULT;

    public static Class<? extends ChatMessage> classOf(ChatMessageType type){
        switch (type){
            case AI:
            case USER:
            case SYSTEM:
            case TOOL_EXECUTION_RESULT:
            default:
                throw illegalArgument("unknow ChatMessageType: %s",type);
        }
    }
}
