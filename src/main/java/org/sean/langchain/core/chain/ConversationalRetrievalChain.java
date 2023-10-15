package org.sean.langchain.core.chain;

import lombok.Builder;
import org.sean.langchain.core.data.message.AiMessage;
import org.sean.langchain.core.data.message.UserMessage;
import org.sean.langchain.core.data.prompt.PromptTemplate;
import org.sean.langchain.core.data.segment.TextSegment;
import org.sean.langchain.core.embedding.store.Retriever;
import org.sean.langchain.core.memory.ChatMemory;
import org.sean.langchain.core.memory.InMemoryChatMemoryStore;
import org.sean.langchain.core.memory.MessageWindowChatMemory;
import org.sean.langchain.core.mode.chat.ChatLanguageModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.sean.langchain.core.util.ValidationUtils.ensureNotBlank;
import static org.sean.langchain.core.util.ValidationUtils.ensureNotNull;

public class ConversationalRetrievalChain implements Chain<String,String>{


    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = PromptTemplate.from(
            "Answer the following question to the best of your ability: {{question}}\n" +
                    "\n" +
                    "Base your answer on the following information:\n" +
                    "{{information}}");

    private final ChatLanguageModel chatLanguageModel;
    private final ChatMemory chatMemory;
    private final PromptTemplate promptTemplate;
    private final Retriever<TextSegment> retriever;

    @Builder
    public ConversationalRetrievalChain(ChatLanguageModel chatLanguageModel,
                                        ChatMemory chatMemory,
                                        PromptTemplate promptTemplate,
                                        Retriever<TextSegment> retriever) {
        this.chatLanguageModel = ensureNotNull(chatLanguageModel, "chatLanguageModel");
        this.chatMemory = chatMemory == null ? MessageWindowChatMemory.builder().store(new InMemoryChatMemoryStore()).build() : chatMemory;
        this.promptTemplate = promptTemplate == null ? DEFAULT_PROMPT_TEMPLATE : promptTemplate;
        this.retriever = ensureNotNull(retriever, "retriever");
    }

    @Override
    public String execute(String question) {

        question = ensureNotBlank(question, "question");

        List<TextSegment> relevantSegments = retriever.findRelevant(question);

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", format(relevantSegments));

        UserMessage userMessage = promptTemplate.apply(variables).toUserMessage();

        chatMemory.add(userMessage);

        AiMessage aiMessage = chatLanguageModel.generate(chatMemory.messages()).content();

        chatMemory.add(aiMessage);

        return aiMessage.text();
    }

    private static String format(List<TextSegment> relevantSegments) {
        return relevantSegments.stream()
                .map(TextSegment::text)
                .map(segment -> "..." + segment + "...")
                .collect(joining("\n\n"));
    }
}
