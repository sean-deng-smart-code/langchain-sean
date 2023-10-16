package org.sean.langchain.core.example;

import org.sean.langchain.core.chain.Chain;
import org.sean.langchain.core.chain.ConversationalRetrievalChain;
import org.sean.langchain.core.data.prompt.PromptTemplate;
import org.sean.langchain.core.data.segment.TextSegment;
import org.sean.langchain.core.document.Document;
import org.sean.langchain.core.document.DocumentSplitter;
import org.sean.langchain.core.document.FileSystemDocumentLoader;
import org.sean.langchain.core.document.splitter.DocumentByLineSplitter;
import org.sean.langchain.core.document.splitter.DocumentSplitters;
import org.sean.langchain.core.embedding.EmbeddingModel;
import org.sean.langchain.core.embedding.OpenAiEmbeddingModel;
import org.sean.langchain.core.embedding.store.EmbeddingStore;
import org.sean.langchain.core.embedding.store.EmbeddingStoreRetriever;
import org.sean.langchain.core.memory.ChatMemory;
import org.sean.langchain.core.memory.InMemoryChatMemoryStore;
import org.sean.langchain.core.memory.MessageWindowChatMemory;
import org.sean.langchain.core.mode.chat.OpenAiChatModel;
import org.sean.langchain.core.token.OpenAiTokenizer;
import org.sean.langchain.core.vectorstore.InMemoryEmbeddingStore;

import java.time.Duration;
import java.util.List;

import static org.sean.langchain.core.constants.OpenAiModelName.GPT_3_5_TURBO_16K;
import static org.sean.langchain.core.constants.OpenAiModelName.TEXT_EMBEDDING_ADA_002;

public class ConversationalRetrievalChainExample {

    public static void main(String[] args) {
        String api_key = "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";
        //document
        Document document = FileSystemDocumentLoader.loadDocument("/Users/sean.deng/temp/news.txt");
        DocumentSplitter spliter = DocumentSplitters.recursive(1000, 0,new OpenAiTokenizer(TEXT_EMBEDDING_ADA_002));
        List<TextSegment> textSgements = spliter.split(document);

        //embeding model
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder().apiKey(api_key).timeout(Duration.ofMinutes(20)).build();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore();
        //vector list <==> text segment,在查找的时候根据vector相似度查找，让后返回原始的text segment
        embeddingStore.addAll(embeddingModel.embedAll(textSgements).content(),textSgements);

        // embeding retriever
        EmbeddingStoreRetriever retriever = new EmbeddingStoreRetriever(embeddingStore,embeddingModel,10,0.3);

        // chat model

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(api_key)
                .modelName(GPT_3_5_TURBO_16K)
                .timeout(Duration.ofMinutes(10))
                .temperature(0.7).build();

        // memort
        ChatMemory memory = new MessageWindowChatMemory("seab",10,new InMemoryChatMemoryStore());

        // prompt


        // chain

        Chain<String,String> chain = new ConversationalRetrievalChain(model,memory,null,retriever);
        String result = chain.execute("帮我用50个子总结一下这个文章");
        System.out.println(result);

    }
}
