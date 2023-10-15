package org.sean.langchain.core.memory;

import org.sean.langchain.core.data.message.ChatMessage;

import java.util.List;

public interface ChatMemoryStore{

    /**
     * Retrieves messages for a specified chat memory.
     *
     * @param memoryId The ID of the chat memory.
     * @return List of messages for the specified chat memory. Must not be null. Can be deserialized from JSON using {@link ChatMessageDeserializer}.
     */
    List<ChatMessage> getMessages(Object memoryId);

    /**
     * Updates messages for a specified chat memory.
     *
     * @param memoryId The ID of the chat memory.
     * @param messages List of messages for the specified chat memory, that represent the current state of the {@link ChatMemory}.
     *                 Can be serialized to JSON using {@link ChatMessageSerializer}.
     */
    void updateMessages(Object memoryId, List<ChatMessage> messages);

    /**
     * Deletes all messages for a specified chat memory.
     *
     * @param memoryId The ID of the chat memory.
     */
    void deleteMessages(Object memoryId);

}


