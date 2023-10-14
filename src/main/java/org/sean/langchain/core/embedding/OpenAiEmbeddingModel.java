package org.sean.langchain.core.embedding;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import lombok.Builder;
import org.sean.langchain.core.data.output.Response;
import org.sean.langchain.core.data.segment.TextSegment;
import org.sean.langchain.core.token.OpenAiTokenizer;
import org.sean.langchain.core.token.TokenCountEstimator;
import org.sean.langchain.core.token.Tokenizer;

import java.net.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;
import static org.sean.langchain.core.constants.OpenAiConstant.*;
import static org.sean.langchain.core.constants.OpenAiModelName.TEXT_EMBEDDING_ADA_002;
import static org.sean.langchain.core.util.OpenApiFunctions.tokenUsageFrom;
import static org.sean.langchain.core.util.RetryUtils.withRetry;

public class OpenAiEmbeddingModel implements EmbeddingModel, TokenCountEstimator {

    private final OpenAiClient client;

    private final String modelName;

    private final Integer maxRetries;

    private final Tokenizer tokenizer;

    @Builder
    public OpenAiEmbeddingModel(String baseUrl,
                                String apiKey,
                                String modelName,
                                Duration timeout,
                                Integer maxRetries,
                                Proxy proxy,
                                Boolean logRequests,
                                Boolean logResponses) {

        baseUrl = baseUrl == null ? OPENAI_URL : baseUrl;
        if (OPENAI_DEMO_API_KEY.equals(apiKey)) {
            baseUrl = OPENAI_DEMO_URL;
        }
        modelName = modelName == null ? TEXT_EMBEDDING_ADA_002 : modelName;
        timeout = timeout == null ? ofSeconds(15) : timeout;
        maxRetries = maxRetries == null ? 3 : maxRetries;

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
        this.maxRetries = maxRetries;
        this.tokenizer = new OpenAiTokenizer(this.modelName);
    }


    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> singletonList) {
        List<String> text = singletonList.stream().map(TextSegment::text).collect(Collectors.toList());
        return embedTexts(text);
    }

    private Response<List<Embedding>> embedTexts(List<String> texts) {

        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(texts)
                .model(modelName)
                .build();

        EmbeddingResponse response = withRetry(() -> client.embedding(request).execute(), maxRetries);

        List<Embedding> embeddings = response.data().stream()
                .map(openAiEmbedding -> Embedding.from(openAiEmbedding.embedding()))
                .collect(toList());

        return Response.from(
                embeddings,
                tokenUsageFrom(response.usage())
        );
    }

    @Override
    public int estimateTokenCount(String text) {
        return tokenizer.estimateTokenCountInText(text);
    }
}

