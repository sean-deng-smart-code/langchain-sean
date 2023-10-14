package org.sean.langchain.core.data.message;

import org.sean.langchain.core.constants.ChatMessageType;

public abstract class ChatMessage {

    protected final String text;

    ChatMessage(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public abstract ChatMessageType type();

}
