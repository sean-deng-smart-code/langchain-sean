package org.sean.langchain.core.embedding;

import org.sean.langchain.core.data.output.Response;
import org.sean.langchain.core.data.segment.TextSegment;

import java.util.Collections;
import java.util.List;

public interface EmbeddingModel {

    default Response<Embedding> embed(String text){
        return embed(TextSegment.from(text));
    }

    default Response<Embedding> embed(TextSegment segment){
        Response<List<Embedding>> response =  embedAll(Collections.singletonList(segment));
        return Response.from(response.content().get(0),response.tokenUsage(),response.finishReason());

    }

    Response<List<Embedding>> embedAll(List<TextSegment> singletonList);
}
