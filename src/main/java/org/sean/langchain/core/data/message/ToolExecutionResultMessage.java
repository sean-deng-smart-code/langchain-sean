package org.sean.langchain.core.data.message;

import org.sean.langchain.core.constants.ChatMessageType;

import java.util.Objects;

import static org.sean.langchain.core.constants.ChatMessageType.TOOL_EXECUTION_RESULT;
import static org.sean.langchain.core.util.Utils.quoted;

public class ToolExecutionResultMessage extends ChatMessage{
    private final String toolName;

    public ToolExecutionResultMessage(String toolName, String toolExecutionResult) {
        super(toolExecutionResult);
        this.toolName = toolName;
    }

    public String toolName() {
        return toolName;
    }

    @Override
    public ChatMessageType type() {
        return TOOL_EXECUTION_RESULT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolExecutionResultMessage that = (ToolExecutionResultMessage) o;
        return Objects.equals(this.toolName, that.toolName)
                && Objects.equals(this.text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toolName, text);
    }

    @Override
    public String toString() {
        return "ToolExecutionResultMessage {" +
                " toolName = " + quoted(toolName) +
                " text = " + quoted(text) +
                " }";
    }

    public static ToolExecutionResultMessage from(String toolName, String toolExecutionResult) {
        return new ToolExecutionResultMessage(toolName, toolExecutionResult);
    }

    public static ToolExecutionResultMessage toolExecutionResultMessage(String toolName, String toolExecutionResult) {
        return from(toolName, toolExecutionResult);
    }
}
