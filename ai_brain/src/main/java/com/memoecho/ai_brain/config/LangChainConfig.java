package com.memoecho.ai_brain.config;

import com.memoecho.ai_brain.prompt.ScheduleExtractor;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

@Configuration
@Slf4j(topic = "LangChain配置")
public class LangChainConfig {
    // ===============  AI 模型 ==================
    @Value("${memo-echo.ai.dashscope.api-key}")
    private String apiKey;
    @Value("${memo-echo.ai.dashscope.base-url}")
    private String baseUrl;
    @Value("${memo-echo.ai.dashscope.model-name}")
    private String modelName;
    @Value("${memo-echo.ai.dashscope.chat-model-name:qwen-plus}")
    private String chatModelName;
    @Value("${memo-echo.ai.dashscope.dimension:512}")
    private int dimension;

    // ===============  Qdrant 数据库 ==================
    @Value("${memo-echo.ai.qdrant.host}")
    private String host;
    @Value("${memo-echo.ai.qdrant.port}")
    private int port;
    @Value("${memo-echo.ai.qdrant.collection-name}")
    private String collectionName;

    @PostConstruct
    public void initQdrant(){
        // 临时建立rpc链接，执行建表
        QdrantClient adminClient =  new QdrantClient(
                QdrantGrpcClient.newBuilder(host,port,false).build()
        );

        try{
            boolean exists = adminClient.collectionExistsAsync(collectionName).get();
            if(!exists){
                adminClient.createCollectionAsync(
                        collectionName,
                        Collections.VectorParams.newBuilder()
                                .setSize(dimension)
                                .setDistance(Collections.Distance.Cosine) // 余弦相似
                                .build()
                ).get();
                log.info("初始化向量数据库成功,数据库名字：{},纬度：{}"
                        ,collectionName,dimension);
            }
        } catch (Exception e){
            log.warn("Qdrant 向量数据库初始化异常，请检查。",e);
        } finally {
            adminClient.close();
        }
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(){
        return QdrantEmbeddingStore.builder()
                .host(host).port(port).collectionName(collectionName)
                .build();
    }

    // 注册向量模型
    @Bean
    public EmbeddingModel embeddingModel(){
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey).baseUrl(baseUrl)
                .modelName(modelName).dimensions(dimension)
                .build();
    }

    // 注册对话模型
    @Bean
    public ChatModel chatModel(){
        return OpenAiChatModel.builder()
                .apiKey(apiKey).baseUrl(baseUrl)
                .modelName(chatModelName)
                .build();
    }

    // 注册ai服务,springboot 会自动注册上面的对话模型
    @Bean
    public ScheduleExtractor scheduleExtractor(ChatModel chatModel){
        return AiServices.builder(ScheduleExtractor.class)
                .chatModel(chatModel)
                .build();
    }
}
