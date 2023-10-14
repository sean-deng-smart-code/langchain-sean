package org.sean.langchain.core.data.message;

import org.sean.langchain.core.constants.ChatMessageType;

import java.util.Objects;

import static org.sean.langchain.core.constants.ChatMessageType.SYSTEM;
import static org.sean.langchain.core.util.Utils.quoted;

public class SystemMessage extends ChatMessage{
    public SystemMessage(String text) {
        super(text);
    }

    @Override
    public ChatMessageType type() {
        return SYSTEM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemMessage that = (SystemMessage) o;
        return Objects.equals(this.text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "SystemMessage {" +
                " text = " + quoted(text) +
                " }";
    }

    public static SystemMessage from(String text) {
        return new SystemMessage(text);
    }

    public static SystemMessage systemMessage(String text) {
        return from(text);
    }
}
