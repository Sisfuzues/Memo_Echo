package com.memoecho.ai_brain.config;

import com.memoecho.ai_brain.prompt.ScheduleExtractor;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {
    // ===============  AI 模型 ==================
    @Value("${memo-echo.ai.dashscope.api-key}")
    private String apiKey;
    @Value("${memo-echo.ai.dashscope.base-url}")
    private String baseUrl;
    @Value("${memo-echo.ai.dashscope.model-name}")
    private String modelName;
    @Value("${memo-echo.ai.dashscope.chat-model-name}")
    private String chatModelName;
    @Value("${memo-echo.ai.dashscope.dimension:512}")
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

    // 注册ai服务,springboot 会自动注册上面的对话模型
    @Bean
    public ScheduleExtractor scheduleExtractor(ChatModel chatModel){
        return AiServices.builder(ScheduleExtractor.class)
                .chatModel(chatModel)
                .build();
    }
}
