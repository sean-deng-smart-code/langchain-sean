package org.sean.langchain.core.example;

import org.sean.langchain.core.chain.ConversationalChain;
import org.sean.langchain.core.memory.ChatMemory;
import org.sean.langchain.core.memory.ChatMemoryStore;
import org.sean.langchain.core.memory.InMemoryChatMemoryStore;
import org.sean.langchain.core.memory.MessageWindowChatMemory;
import org.sean.langchain.core.mode.chat.OpenAiChatModel;

import java.time.Duration;

import static org.sean.langchain.core.constants.OpenAiModelName.GPT_3_5_TURBO_16K;

public class ConversationalChainExample {
    public static void main(String[] args) {
        String apiKey = "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";

        // build model
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(GPT_3_5_TURBO_16K)
                .timeout(Duration.ofMinutes(10)).build();

        // build memory
        ChatMemory memory = MessageWindowChatMemory.builder()
                .id("sean")
                .maxMessages(10)
                .store(new InMemoryChatMemoryStore()).build();

        // build chain

        ConversationalChain chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(memory).build();


        String q1 = "你是一个优秀的程序员，你将协助用户完成编程任务";
        String q2 = "帮我写一个java程序，实现文件拷贝";

        chain.execute(q1);
        System.out.println(chain.execute(q2));

    }
}
