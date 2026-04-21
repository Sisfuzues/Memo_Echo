package com.memoecho.ai_brain.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {
    // ===============  AI 模型 ==================
    @Value("memo-echo.ai.dashscope.api-key")
    private String apiKey;
    @Value("memo-echo.ai.dashscope.base-url")
    private String baseUrl;
    @Value("memo-echo.ai.dashscope.model-name")
    private String modelName;
    @Value("memo-echo.ai.dashscope.chat-model-name")
    private String chatModelName;
    @Value("memo-echo.ai.dashscope.dimension")
    private int dimension;


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


}

/*
memo-echo:
  ai:
    qdrant:
      host: 127.0.0.1                            # 向量数据库地址，这里我们真实部署要替换成真实的网址
      port: 6334                                 # 端口
      collection-name: schedule_embeddings       # 向量表名字
 */