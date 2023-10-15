package org.sean.langchain.core.embedding.store;

import org.sean.langchain.core.data.segment.TextSegment;
import org.sean.langchain.core.embedding.Embedding;
import org.sean.langchain.core.embedding.EmbeddingModel;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class EmbeddingStoreRetriever implements Retriever<TextSegment>{

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final int maxResults;
    private final Double minScore;

    public EmbeddingStoreRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                   EmbeddingModel embeddingModel,
                                   int maxResults,
                                   Double minScore) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
        this.maxResults = maxResults;
        this.minScore = minScore;
    }




    @Override
    public List<TextSegment> findRelevant(String text) {
        Embedding embeddedText = embeddingModel.embed(text).content();

        List<EmbeddingMatch<TextSegment>> relevant;
        if (minScore == null) {
            relevant = embeddingStore.findRelevant(embeddedText, maxResults);
        } else {
            relevant = embeddingStore.findRelevant(embeddedText, maxResults, minScore);
        }

        return relevant.stream()
                .map(EmbeddingMatch::embedded)
                .collect(toList());
    }

    public static EmbeddingStoreRetriever from(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return new EmbeddingStoreRetriever(embeddingStore, embeddingModel, 2, null);
    }

    public static EmbeddingStoreRetriever from(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel, int maxResults) {
        return new EmbeddingStoreRetriever(embeddingStore, embeddingModel, maxResults, null);
    }

    public static EmbeddingStoreRetriever from(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel, int maxResults, double minScore) {
        return new EmbeddingStoreRetriever(embeddingStore, embeddingModel, maxResults, minScore);
    }
}
