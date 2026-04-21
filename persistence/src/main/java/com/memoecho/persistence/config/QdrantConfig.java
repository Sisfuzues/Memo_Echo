package com.memoecho.persistence.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantConfig {
    // ===============  Qdrant 数据库 ==================
    @Value("memo-echo.ai.qdrant.host")
    private String host;
    @Value("memo-echo.ai.qdrant.port")
    private int port;
    @Value("memo-echo.ai.qdrant.collection-name")
    private String collectionName;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(){
        return QdrantEmbeddingStore.builder()
                .host(host).port(port).collectionName(collectionName)
                .build();
    }
}
