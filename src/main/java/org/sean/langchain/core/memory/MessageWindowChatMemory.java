package org.sean.langchain.core.memory;

import lombok.Builder;
import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.data.message.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageWindowChatMemory implements ChatMemory{
    private static final Logger log = LoggerFactory.getLogger(MessageWindowChatMemory.class);

    private Object id = "default";
    private final Integer maxMessages;
    private final ChatMemoryStore store;

    @Builder
    public MessageWindowChatMemory(Object id, Integer maxMessages, ChatMemoryStore store) {
        this.id = id;
        this.maxMessages = maxMessages;
        this.store = store;
    }


    @Override
    public Object id() {
        return id;
    }

    @Override
    public void add(ChatMessage message) {
        List<ChatMessage> messages = messages();
        if (message instanceof SystemMessage) {
            Optional<SystemMessage> systemMessage = findSystemMessage(messages);
            if (systemMessage.isPresent()) {
                if (systemMessage.get().equals(message)) {
                    return; // do not add the same system message
                } else {
                    messages.remove(systemMessage.get()); // need to replace existing system message
                }
            }
        }
        messages.add(message);
        ensureCapacity(messages, maxMessages);
        store.updateMessages(id, messages);

    }

    @Override
    public List<ChatMessage> messages() {
        List<ChatMessage> messages = new ArrayList<>(store.getMessages(id));
        ensureCapacity(messages, maxMessages);
        return messages;
    }

    @Override
    public void clear() {
        store.deleteMessages(id);
    }

    private static Optional<SystemMessage> findSystemMessage(List<ChatMessage> messages) {
        return messages.stream()
                .filter(message -> message instanceof SystemMessage)
                .map(message -> (SystemMessage) message)
                .findAny();
    }

    private static void ensureCapacity(List<ChatMessage> messages, int maxMessages) {
        while (messages.size() > maxMessages) {
            int messageToRemove = 0;
            if (messages.get(0) instanceof SystemMessage) {
                messageToRemove = 1;
            }
            ChatMessage removedMessage = messages.remove(messageToRemove);
            log.trace("Removing the following message to comply with the capacity requirements: {}", removedMessage);
        }
    }

}
