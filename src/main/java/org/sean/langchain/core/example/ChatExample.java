package org.sean.langchain.core.example;

import org.sean.langchain.core.mode.chat.OpenAiChatModel;

public class ChatExample {

    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("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").build();
        String result = model.generate("帮我写一个简答的java demo，输出hellowrold");
        System.out.println(result);
    }
}
