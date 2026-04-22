package AIDEMO;

import dev.langchain4j.data.message.UserMessage;

/*实现服务的模块
* ：传入文本message化模块--langchain直接用      。
* 验证日程信息模块                             ！
* 向量嵌入与入库控制模块                        ！
*
* @ 消息读取与工具调用模块（负责读取@ 判断是否明显不是日程问题决定调用RAG工具回复流还是调用直接回复流）
* 查询消息向量化与数据检索器
* RAG+查询接收与回复生成模块*/
public class AgentModule {


    protected String scheduleValidator(String userText){//可能要将类型转化分离  并且标注成agent并用langchain调用
        return "";
    }
}
