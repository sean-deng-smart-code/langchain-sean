package org.sean.langchain.core.example;

import org.sean.langchain.core.mode.chat.OpenAiChatModel;

import java.time.Duration;

public class ChatExample {

    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("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").timeout(Duration.ofMinutes(2)).build();
        String result = model.generate("帮我写一个简答的java demo，输出hellowrold");
        System.out.println(result);
    }
}
