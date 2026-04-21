# ReadMe
## 快速启动
如果你想测试**bot_gateway** 模块的话，你首先需要在本地用*docker*部署一个**Napcat**。
详细的部署流程太繁琐，我这里写一个临时的*docker compose*文件。

```yaml
services:
      napcat:
        image: mlikiowa/napcat-docker:latest
        container_name: memo_echo_napcat
        environment:
          # ⚠️ 注意：这里填你的 SpringBoot 网关接收地址
          - WEBHOOK_URL=http://localhost:8080/api/bot/webhook
        volumes:
          - ./napcat/config:/app/napcat/config
          - ./napcat/qq:/app/.config/QQ
        ports:
          - "6099:6099"
          - "3011:3011"
        restart: always
```
然后你需要在这个文件所在的文件夹用命令行输入如下命令：
```bash
sudo docker compose up -d 
```
这个是启动**Napcat**,然后你需要查看日志：
```bash
sudo docker logs --tail 50 memo_echo_napcat
```
扫码登录机器人帐号，随后去这个网址更改配置：[Napcat_UI](http://127.0.0.1:6099/webui/)。
具体配置如下：
![img.png](img.png)
![img_1.png](img_1.png)
![img_2.png](img_2.png)

## 流程图
```mermaid
flowchart TD
    %% ==========================================
    %% 1. 全局样式定义 (基于 Tailwind 柔和色系)
    %% ==========================================
    classDef mq_bus fill:#e0f2fe,stroke:#0284c7,stroke-width:3px,color:#0369a1
    classDef endpoint fill:#f1f5f9,stroke:#475569,stroke-width:2px,color:#1e293b
    classDef filter_node fill:#fff7ed,stroke:#ea580c,stroke-width:2px,color:#9a3412
    classDef ai_node fill:#f3e8ff,stroke:#9333ea,stroke-width:2px,color:#6b21a8
    classDef sync_node fill:#fce4ec,stroke:#e11d48,stroke-width:2px,color:#be123c
    classDef db_node fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#15803d
    classDef condition fill:#ffffff,stroke:#64748b,stroke-width:2px,color:#334155

    direction TD

    %% ==========================================
    %% 2. 模块拓扑图
    %% ==========================================
    
    subgraph BG ["🌐 0. 机器人网关 (Bot Gateway)"]
        direction TB
        BG_Read(["消息接入 (Read)"]) --> BG_At{"是否为@指令?"}
        BG_At -- "否" --> BG_Rec["进入日程录入流程"]
        BG_At -- "是" --> BG_Ign["进入消息回复流程"]
        BG_Resp(["消息回发 (Send)"])
    end

    MQ_Hub(("🔀 MQ 消息总线<br/>(RabbitMQ)"))

    subgraph SF ["🛡️ 1. 边缘风控层 (Sensitive Filter)"]
        direction TB
        SF_Proc["监听: group_msg_received"] --> SF_AC{"AC 自动机引擎"}
        SF_AC -- "安全" --> SF_OK["封装 Filtered DTO"]
        SF_AC -- "危险" --> SF_Err["封装 Unsafe DTO"]
    end

    subgraph AI ["🧠 2. 智能解析层 (AI Brain)"]
        direction TB
        AI_Listen["监听: msg_filtered"] --> Is_at{"是否为查询信息?"}
        Is_at -- "是" --> Find["生成 Query 向量"]
        Is_at -- "否" --> Keep["整理为日程 AI_DTO"]
    end

    subgraph DB ["🗄️ 3. 存储与持久化 (Persistence)"]
        direction TB
        DB_Qdrant[("🔺 Qdrant 向量库")]
        DB_MySQL[("🛢️ MySQL 核心表")]

        DB_Listen["监听: msg_maked"] --> DB_Qdrant
        DB_Qdrant -- "主键 ID 映射" --> DB_MySQL
        DB_Save["监听: unsafe"] --> DB_MySQL
        DB_MySQL -. "数据同步" .-> DB_Redis[("⚡ Redis 缓存")]
    end

    %% ==========================================
    %% 3. 全局链路连线 (协议语义化)
    %% ==========================================
    
    %% [异步 MQ 链路] - 使用蓝色虚线
    BG_Rec -. "pub: msg_received" .-> MQ_Hub
    BG_Ign -. "pub: msg_received" .-> MQ_Hub
    
    MQ_Hub -. "sub" .-> SF_Proc
    SF_OK  -. "pub: msg_filtered" .-> MQ_Hub
    SF_Err -. "pub: msg_unsafe" .-> MQ_Hub
    
    MQ_Hub -. "sub" .-> AI_Listen
    MQ_Hub -. "sub" .-> DB_Save
    Keep   -. "pub: msg_maked" .-> MQ_Hub
    MQ_Hub -. "sub" .-> DB_Listen
    
    Find   -. "pub: msg_result" .-> MQ_Hub
    MQ_Hub -. "sub" .-> BG_Resp

    %% [同步 gRPC 链路] - 使用红色加粗实线
    Find == "gRPC 阻塞检索" ==> DB_Qdrant

    %% ==========================================
    %% 4. 样式应用
    %% ==========================================
    class MQ_Hub mq_bus;
    class BG_Read,BG_Rec,BG_Ign,BG_Resp endpoint;
    class SF_Proc,SF_OK,SF_Err filter_node;
    class AI_Listen,Keep ai_node;
    class Find sync_node;
    class DB_Listen,DB_Save,DB_Qdrant,DB_MySQL,DB_Redis db_node;
    class BG_At,SF_AC,Is_at condition;
    
    %% 给边框加上柔和的背景
    style BG fill:#f8fafc,stroke:#cbd5e1,stroke-width:2px,stroke-dasharray: 5 5
    style SF fill:#f8fafc,stroke:#cbd5e1,stroke-width:2px,stroke-dasharray: 5 5
    style AI fill:#f8fafc,stroke:#cbd5e1,stroke-width:2px,stroke-dasharray: 5 5
    style DB fill:#f8fafc,stroke:#cbd5e1,stroke-width:2px,stroke-dasharray: 5 5
```
