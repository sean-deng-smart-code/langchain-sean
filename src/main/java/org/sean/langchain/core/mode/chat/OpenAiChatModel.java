package org.sean.langchain.core.mode.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import lombok.Builder;
import org.sean.langchain.core.data.message.AiMessage;
import org.sean.langchain.core.data.message.ChatMessage;
import org.sean.langchain.core.data.output.Response;
import org.sean.langchain.core.token.OpenAiTokenizer;
import org.sean.langchain.core.token.Tokenizer;
import org.sean.langchain.core.tool.ToolSpecification;

import java.net.Proxy;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.sean.langchain.core.constants.OpenAiConstant.*;
import static org.sean.langchain.core.constants.OpenAiModelName.GPT_3_5_TURBO;
import static org.sean.langchain.core.util.OpenApiFunctions.*;
import static org.sean.langchain.core.util.RetryUtils.withRetry;
import static org.sean.langchain.core.util.Utils.getOrDefault;

public class OpenAiChatModel implements ChatLanguageModel,TokenCountEstimator{
    private final OpenAiClient client;
    private final String modelName;
    private final Double temperature;
    private final Double topP;
    private final List<String> stop;
    private final Integer maxTokens;
    private final Double presencePenalty;
    private final Double frequencyPenalty;
    private final Integer maxRetries;
    private final Tokenizer tokenizer;

    @Builder
    public OpenAiChatModel(String baseUrl,
                           String apiKey,
                           String modelName,
                           Double temperature,
                           Double topP,
                           List<String> stop,
                           Integer maxTokens,
                           Double presencePenalty,
                           Double frequencyPenalty,
                           Duration timeout,
                           Integer maxRetries,
                           Proxy proxy,
                           Boolean logRequests,
                           Boolean logResponses) {

        baseUrl = getOrDefault(baseUrl, OPENAI_URL);
        if (OPENAI_DEMO_API_KEY.equals(apiKey)) {
            baseUrl = OPENAI_DEMO_URL;
        }
        modelName = getOrDefault(modelName, GPT_3_5_TURBO);
        timeout = getOrDefault(timeout, defaultTimeoutFor(modelName));

        this.client = OpenAiClient.builder()
                .openAiApiKey(apiKey)
                .baseUrl(baseUrl)
                .callTimeout(timeout)
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .proxy(proxy)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
        this.modelName = modelName;
        this.temperature = getOrDefault(temperature, 0.7);
        this.topP = topP;
        this.stop = stop;
        this.maxTokens = maxTokens;
        this.presencePenalty = presencePenalty;
        this.frequencyPenalty = frequencyPenalty;
        this.maxRetries = getOrDefault(maxRetries, 3);
        this.tokenizer = new OpenAiTokenizer(this.modelName);
    }



    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        return generate(messages, null, null);
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, List<ToolSpecification> tools) {
        return generate(messages,tools,null);
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages, ToolSpecification tool) {
        return generate(messages, Collections.singletonList(tool),null);
    }

    @Override
    public int estimateTokenCount(List<ChatMessage> messages) {
        return tokenizer.estimateTokenCountInMessages(messages);
    }

    private Response<AiMessage> generate(List<ChatMessage> messages,
                                         List<ToolSpecification> toolSpecifications,
                                         ToolSpecification toolThatMustBeExecuted) {
        ChatCompletionRequest.Builder requestBuilder = ChatCompletionRequest.builder()
                .model(modelName)
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .topP(topP)
                .stop(stop)
                .maxTokens(maxTokens)
                .presencePenalty(presencePenalty)
                .frequencyPenalty(frequencyPenalty);

        if (toolSpecifications != null && !toolSpecifications.isEmpty()) {
            requestBuilder.functions(toFunctions(toolSpecifications));
        }
        if (toolThatMustBeExecuted != null) {
            requestBuilder.functionCall(toolThatMustBeExecuted.name());
        }

        ChatCompletionRequest request = requestBuilder.build();

        ChatCompletionResponse response = withRetry(() -> client.chatCompletion(request).execute(), maxRetries);

        return Response.from(
                aiMessageFrom(response),
                tokenUsageFrom(response.usage()),
                finishReasonFrom(response.choices().get(0).finishReason())
        );
    }
}
