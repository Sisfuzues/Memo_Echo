package chatAgentDemo_1;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiModerationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CAgentdemo_1 {
    private static OpenAiChatModel chatModel = OpenAiChatModel.builder()
            .apiKey("sk-363167240c50469c823d9e2e72ba8da8")
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .modelName("qwen-max")
            //.temperature(0.3)//模型温度应靠近0
            .build();

    private static OpenAiModerationModel moderationModel = OpenAiModerationModel.builder()
            .build();

    //chat方法返回一个ChatResponse类型，结构是aiMessage.text/thinking/toolExecutionRequests/attributes
    public static void chatF_1(){
        UserMessage userMessage_1 = UserMessage.from("十个字介绍自己");
        ChatResponse chatresponse_1 = chatModel.chat(userMessage_1);
        System.out.println(chatresponse_1.aiMessage().text());//回复生成模块只需获取text文本并注入对应格式
    }

    public static void chatF_2(){
        List<ChatMessage> chatMessages = new ArrayList<>();
        SystemMessage systemMessage_1 =SystemMessage.from("""
                你作为日程总结智能体的最终回复生成模块，你需要将召回文本组织并整理成日程安排（在这一过程中确保信息与文本一致，并且应避免输出不当的言论）
                
                你会得到类似以下格式的召回文本：
                
                1.text
                2.text
                3.text
                ··· ···
                
                """);
        chatMessages.add(systemMessage_1);
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                chatMessages.add(
                        UserMessage.from(
                                scanner.nextLine()
                        )
                );
                ChatResponse chatResponse_2 = chatModel.chat(chatMessages);
                System.out.println(chatResponse_2.aiMessage().text());
                chatMessages.add(chatResponse_2.aiMessage());//aimessage加入messages队列
            }
        }finally {
            scanner.close();
        }

    }

    public static void main(String[] args) {
        //chatF_1();
        chatF_2();
    }
}
