package org.sean.langchain.core.mode.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionChoice;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.chat.Delta;
import lombok.Builder;
import org.sean.langchain.core.data.message.AiMessage;
import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.data.output.OpenAiStreamingResponseBuilder;
import org.sean.langchain.core.data.output.Response;
import org.sean.langchain.core.data.output.StreamingResponseHandler;
import org.sean.langchain.core.token.OpenAiTokenizer;
import org.sean.langchain.core.token.Tokenizer;
import org.sean.langchain.core.tool.ToolSpecification;

import java.net.Proxy;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singletonList;
import static org.sean.langchain.core.constants.OpenAiConstant.OPENAI_URL;
import static org.sean.langchain.core.constants.OpenAiModelName.GPT_3_5_TURBO;
import static org.sean.langchain.core.util.OpenApiFunctions.toFunctions;
import static org.sean.langchain.core.util.OpenApiFunctions.toOpenAiMessages;
import static org.sean.langchain.core.util.Utils.getOrDefault;

public class OpenAiStreamChatModel implements StreamChatLanguageModel,TokenCountEstimator {

    private final OpenAiClient client;
    private final String modelName;
    private final Double temperature;
    private final Double topP;
    private final List<String> stop;
    private final Integer maxTokens;
    private final Double presencePenalty;
    private final Double frequencyPenalty;
    private final Tokenizer tokenizer;

    @Builder
    public OpenAiStreamChatModel(String baseUrl,
                                 String apiKey,
                                 String modelName,
                                 Double temperature,
                                 Double topP,
                                 List<String> stop,
                                 Integer maxTokens,
                                 Double presencePenalty,
                                 Double frequencyPenalty,
                                 Duration timeout,
                                 Proxy proxy,
                                 Boolean logRequests,
                                 Boolean logResponses) {

        timeout = getOrDefault(timeout, ofSeconds(5));

        this.client = OpenAiClient.builder()
                .baseUrl(getOrDefault(baseUrl, OPENAI_URL))
                .openAiApiKey(apiKey)
                .callTimeout(timeout)
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .proxy(proxy)
                .logRequests(logRequests)
                .logStreamingResponses(logResponses)
                .build();
        this.modelName = getOrDefault(modelName, GPT_3_5_TURBO);
        this.temperature = getOrDefault(temperature, 0.7);
        this.topP = topP;
        this.stop = stop;
        this.maxTokens = maxTokens;
        this.presencePenalty = presencePenalty;
        this.frequencyPenalty = frequencyPenalty;
        this.tokenizer = new OpenAiTokenizer(this.modelName);
    }

    @Override
    public void generate(StreamingResponseHandler<AiMessage> messageHandler, List<ChatMessage> messages) {
        generate(messages,null,null,messageHandler);
    }

    @Override
    public void generate(StreamingResponseHandler<AiMessage> messageHandler, List<ChatMessage> messages, List<ToolSpecification> tools) {
        generate(messages,tools,null,messageHandler);
    }

    @Override
    public void generate(StreamingResponseHandler<AiMessage> messageHandler, List<ChatMessage> messages, ToolSpecification tool) {
        generate(messages, Collections.singletonList(tool),null,messageHandler);
    }


    private void generate(List<ChatMessage> messages,
                          List<ToolSpecification> toolSpecifications,
                          ToolSpecification toolThatMustBeExecuted,
                          StreamingResponseHandler<AiMessage> handler) {
        ChatCompletionRequest.Builder requestBuilder = ChatCompletionRequest.builder()
                .stream(true)
                .model(modelName)
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .topP(topP)
                .stop(stop)
                .maxTokens(maxTokens)
                .presencePenalty(presencePenalty)
                .frequencyPenalty(frequencyPenalty);

        int inputTokenCount = tokenizer.estimateTokenCountInMessages(messages);

        if (toolSpecifications != null && !toolSpecifications.isEmpty()) {
            requestBuilder.functions(toFunctions(toolSpecifications));
            inputTokenCount += tokenizer.estimateTokenCountInToolSpecifications(toolSpecifications);
        }
        if (toolThatMustBeExecuted != null) {
            requestBuilder.functionCall(toolThatMustBeExecuted.name());
            inputTokenCount += tokenizer.estimateTokenCountInToolSpecification(toolThatMustBeExecuted);
        }

        ChatCompletionRequest request = requestBuilder.build();

        OpenAiStreamingResponseBuilder responseBuilder = new OpenAiStreamingResponseBuilder(inputTokenCount);

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    responseBuilder.append(partialResponse);
                    handle(partialResponse, handler);
                })
                .onComplete(() -> {
                    Response<AiMessage> response = responseBuilder.build();
                    handler.onComplete(response);
                })
                .onError(handler::onError)
                .execute();
    }

    private static void handle(ChatCompletionResponse partialResponse,
                               StreamingResponseHandler<AiMessage> handler) {
        List<ChatCompletionChoice> choices = partialResponse.choices();
        if (choices == null || choices.isEmpty()) {
            return;
        }
        Delta delta = choices.get(0).delta();
        String content = delta.content();
        if (content != null) {
            handler.onNext(content);
        }
    }

    @Override
    public int estimateTokenCount(List<ChatMessage> messages) {
        return tokenizer.estimateTokenCountInMessages(messages);
    }


}
