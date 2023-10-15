package org.sean.langchain.core.embedding.store;

import java.util.List;

public interface Retriever<T> {

    /**
     * 获取最相似的结果
     * @param text
     * @return
     */
    List<T> findRelevant(String text);
}
