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
    %% 全局方向：从上到下，但在子图内可以微调
    direction TD

    %% 阶段零:前端协议层
    subgraph BG ["0. 机器人网关 (Bot Gateway)"]
        direction TB
        BG_Read["消息接入 (Read)"] --> BG_At{"是否为@指令?"}
        BG_At -- "否" --> BG_Rec["进入日程录入流程"]
        BG_At -- "是" --> BG_Ign["进入消息回复流程"]
        BG_Resp["消息回发 (Send)"]
    end

    %% 阶段二：消息总线（作为逻辑中心）
    MQ_Hub(("MQ 消息队列总线"))

    %% 阶段一：风控过滤层
    subgraph SF ["1. 边缘拦截层 (Sensitive Filter)"]
        direction TB
        SF_Proc["监听: group_msg_received"] --> SF_AC{"AC 自动机引擎"}
        SF_AC -- "安全" --> SF_OK["封装 Filtered DTO"]
        SF_AC -- "危险" --> SF_Err["封装 Unsafe DTO"]
    end

    %% 阶段三：核心解析层
    subgraph AI ["2. 智能解析层 (AI Brain)"]
        direction TB
       
    end

    %% 阶段四：数据存储层
    subgraph DB ["3. 存储与持久化 (Persistence)"]
        direction TB
        DB_Save["监听: extracted / unsafe"] --> DB_MySQL[("MySQL 核心表")]
        DB_MySQL --> DB_Redis["Redis 缓存同步"]
    end

    %% === 全局数据流转链路 ===
    BG_Rec -->|pub: msg_received| MQ_Hub
    MQ_Hub -->|sub| SF_Proc
    
    SF_OK  -->|pub: msg_filtered| MQ_Hub
    SF_Err -->|pub: msg_unsafe| MQ_Hub
    
    MQ_Hub -->|sub| AI_Listen
    AI_DTO -->|pub: msg_maked| MQ_Hub
    
    MQ_Hub -->|sub| DB_Save
    DB_Redis -->|notify| BG_Resp

    BG_Ign -->|pub: msg_received| MQ_Hub

    %% 样式美化：采用冷峻且专业的色调
    style MQ_Hub fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    style SF_AC fill:#fff3e0,stroke:#e65100,color:#e65100
    style AI_Prompt fill:#f3e5f5,stroke:#4a148c,color:#4a148c
    style DB_MySQL fill:#e8f5e9,stroke:#1b5e20,color:#1b5e20
    style BG_Resp fill:#fce4ec,stroke:#880e4f,stroke-dasharray: 5 5
```
