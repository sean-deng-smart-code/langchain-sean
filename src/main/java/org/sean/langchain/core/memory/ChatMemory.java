package org.sean.langchain.core.memory;

import org.sean.langchain.core.data.message.ChatMessage;

import java.util.List;

public interface ChatMemory {
    Object id();

    void add(ChatMessage message);

    List<ChatMessage> messages();

    void clear();
}
