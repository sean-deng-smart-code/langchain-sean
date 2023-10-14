package org.sean.langchain.core.token;

import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.tool.ToolSpecification;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.sean.langchain.core.tool.ToolSpecifications.toolSpecificationsFrom;

public interface Tokenizer {

    int estimateTokenCountInText(String text);

    int estimateTokenCountInMessage(ChatMessage message);

    int estimateTokenCountInMessages(Iterable<ChatMessage> messages);

    default int estimateTokenCountInTools(Object objectWithTools) {
        return estimateTokenCountInTools(singletonList(objectWithTools));
    }

    default int estimateTokenCountInTools(Iterable<Object> objectsWithTools) {
        List<ToolSpecification> toolSpecifications = new ArrayList<>();
        objectsWithTools.forEach(objectWithTools ->
                toolSpecifications.addAll(toolSpecificationsFrom(objectWithTools)));
        return estimateTokenCountInToolSpecifications(toolSpecifications);
    }

    default int estimateTokenCountInToolSpecification(ToolSpecification toolSpecification) {
        return estimateTokenCountInToolSpecifications(singletonList(toolSpecification));
    }

    int estimateTokenCountInToolSpecifications(Iterable<ToolSpecification> toolSpecifications);

}
