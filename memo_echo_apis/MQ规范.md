# Memo Echo | 内部消息规范

**文档摘要**：本规范定义了系统内部微服务之间通过 MQ 进行异步通信的事件契约。各微服务必须严格按照约定的 Topic、Tag 和 Payload 结构进行生产与消费。

---

## 0. 全局 Payload 规范 (Base Envelope)
为保证文档排版紧凑与高可读性，本规范已提取所有 MQ 消息的公共外壳。下文各个独立事件中，**仅展示 `data` 字段内部的核心业务载荷 (Data Payload)**。

所有发往 MQ 的真实消息，均需包裹在以下标准外壳中：
```json
{
  "eventId": "uuid-1234-5678",      // 事件唯一追踪ID，用于链路追踪
  "timestamp": 1712345678000,       // 事件发生时间戳
  "data": { 
      /* 各事件具体业务字段，详见下文 */ 
  }
}
```

---

## 1. 群消息转日程

### 1.1 群聊消息待处理事件 
- **事件描述**: `bot_gateway` 收到群聊消息，发送给 `sensitive_filter` 模块进行综合过滤。
- **发布方 (Producer)**: `bot_gateway`
- **订阅方 (Consumer)**: `sensitive_filter`
- **路由坐标**:
  - **Topic / Exchange**: `topic_bot_messages`
  - **Tag / RoutingKey**: `group_msg_received`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "time": 1712345678,
    "self_id": 10001000,
    "post_type": "message",
    "flag": null, 
    "message_type": "group",
    "sub_type": "normal",
    "message_id": -192837465,
    "group_id": 987654321,
    "user_id": 1122334455,
    "raw_message": "明天下午三点在三教开会",
    "sender": {
        "user_id": 1122334455,
        "role": "member"
    }
  }
  ```

### 1.2 过滤后的群消息传递
- **事件描述**: `sensitive_filter` 过滤以及预处理消息后发送给 `ai_brain` 模块进行最后的日程录入。
- **发布方 (Producer)**: `sensitive_filter`
- **订阅方 (Consumer)**: `ai_brain`
- **路由坐标**:
  - **Topic / Exchange**: `topic_bot_messages`
  - **Tag / RoutingKey**: `group_msg_filtered`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "time": 1712345678,
    "self_id": 10001000,
    "post_type": "message",
    "flag": null, 
    "message_type": "group",
    "sub_type": "normal",
    "message_id": -192837465,
    "group_id": 987654321,
    "user_id": 1122334455,
    "raw_message": "明天下午三点在三教开会", //过滤后的消息
    "sender": {
        "user_id": 1122334455,
        "role": "member"
    }
  }
  ```

### 1.3 大模型处理后的群日程
- **事件描述**: `ai_brain` 将处理后的日程发送给 `persistence` 模块进行日程登记。
- **发布方 (Producer)**: `ai_brain`
- **订阅方 (Consumer)**: `persistence`
- **路由坐标**:
  - **Topic / Exchange**: `topic_bot_messages`
  - **Tag / RoutingKey**: `group_msg_extracted`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "original_msg_id": -192837465, // 溯源ID
    "schedule_time": [1712401200000, 1712401200000], // 提取出的具体时间戳
    "location": "三教",            // 提取出的地点
    "event_content": "开会",       // 提取出的事件
    "participants": "全体同学",   // 参与者
    "introduce" : "为了庆祝朱宇翔生日，学校邀请..."
  }
  ```

### 1.4 返回给数据库的敏感词预警
- **事件描述**: `sensistive_filter` 将处理后的敏感信息发送给`persistence` 模块进行敏感信息存储。
- **发布方 (Producer)**: `sensistive_filter`
- **订阅方 (Consumer)**: `persistence`
- **路由坐标**:
  - **Topic / Exchange**: `topic_bot_messages`
  - **Tag / RoutingKey**: `group_msg_unsafe`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "time": 1712345678,
    "self_id": 10001000,
    "post_type": "message",
    "flag": null, 
    "message_type": "group",
    "sub_type": "normal",
    "message_id": -192837465,
    "group_id": 987654321,
    "user_id": 1122334455,
    "raw_message": "明天下午三点在三教开会", // 大模型处
    "sender": {
        "user_id": 1122334455,
        "role": "member"
    }
  }
  ```

---

## 2. 群用户提取日程

### 2.1 群聊@bot日程处理
- **事件描述**: `bot_gateway` 收到群聊消息，发送给 `sensitive_filter` 模块进行需求提取。
- **发布方 (Producer)**: `bot_gateway`
- **订阅方 (Consumer)**: `sensitive_filter`
- **路由坐标**:
  - **Topic / Exchange**: `topic_group_needs`
  - **Tag / RoutingKey**: `group_msg_received`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "time": 1712345678,
    "self_id": 10001000,
    "post_type": "message",
    "flag": null, 
    "message_type": "group",
    "sub_type": "normal",
    "message_id": -192837465,
    "group_id": 987654321,
    "user_id": 1122334455,
    "raw_message": "[CQ:at,qq=123] 明天几点开会",
    "sender": {
        "user_id": 1122334455,
        "role": "member"
    }
  }
  ```

### 2.2 过滤后的群日程提取需求
- **事件描述**: `sensitive_filter` 过滤以及预处理消息后发送给 `ai_brain` 模块进行日程查询语句编写。
- **发布方 (Producer)**: `sensitive_filter`
- **订阅方 (Consumer)**: `ai_brain`
- **路由坐标**:
  - **Topic / Exchange**: `topic_group_needs`
  - **Tag / RoutingKey**: `group_msg_filtered`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "time": 1712345678,
    "self_id": 10001000,
    "post_type": "message",
    "flag": null, 
    "message_type": "group",
    "sub_type": "normal",
    "message_id": -192837465,
    "group_id": 987654321,
    "user_id": 1122334455,
    "raw_message": "明天下午三点在三教开会", //过滤后的消息
    "sender": {
        "user_id": 1122334455,
        "role": "member"
    }
  }
  ```

### 2.3 大模型处理后的群日程查询语句
- **事件描述**: `ai_brain` 将处理后的日程查询语句送给 `persistence` 模块进行日程查询。
- **发布方 (Producer)**: `ai_brain`
- **订阅方 (Consumer)**: `persistence`
- **路由坐标**:
  - **Topic / Exchange**: `topic_group_needs`
  - **Tag / RoutingKey**: `group_msg_processed`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "type" : "sql", // or "fun"
    "query" :{
        "sql" : "select * from .... "
    }
  }
  ```

### 2.4 返回给Bot群日程
- **事件描述**: `persistence` 将处理后的日程发送给 `bot_gateway` 模块进行发送。
- **发布方 (Producer)**: `persistence`
- **订阅方 (Consumer)**: `bot_gateway`
- **路由坐标**:
  - **Topic / Exchange**: `topic_group_needs`
  - **Tag / RoutingKey**: `group_msg_result`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "type" : "user",
    "group_id": "10111",
    "schedules" :[
      {
        "original_msg_id": -192837465, // 溯源ID
        "schedule_time": [1712401200000, 1712401200000],
        "location": "三教",            
        "event_content": "开会",       
        "participants": "全体同学",   
        "introduce" : "为了庆祝朱宇翔生日，学校邀请..."
      }
    ]
  }
  ```

---

## 3. 私聊用户提取日程

### 3.1 私聊bot日程处理
- **事件描述**: `bot_gateway` 收到私聊消息，发送给 `sensitive_filter` 模块进行需求提取。
- **发布方 (Producer)**: `bot_gateway`
- **订阅方 (Consumer)**: `sensitive_filter`
- **路由坐标**:
  - **Topic / Exchange**: `topic_user_needs`
  - **Tag / RoutingKey**: `private_msg_received`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "time": 1712345678,
    "self_id": 10001000,
    "post_type": "message",
    "flag": null, 
    "message_type": "private",
    "sub_type": "normal",
    "message_id": -192837465,
    "user_id": 1122334455,
    "raw_message": "明天几点开会",
    "sender": {
        "user_id": 1122334455,
        "role": "member"
    }
  }
  ```

### 3.2 过滤后的私聊日程提取需求
- **事件描述**: `sensitive_filter` 过滤以及预处理消息后发送给 `ai_brain` 模块进行日程查询语句编写。
- **发布方 (Producer)**: `sensitive_filter`
- **订阅方 (Consumer)**: `ai_brain`
- **路由坐标**:
  - **Topic / Exchange**: `topic_user_needs`
  - **Tag / RoutingKey**: `private_msg_filtered`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "time": 1712345678,
    "self_id": 10001000,
    "post_type": "message",
    "flag": null, 
    "message_type": "private",
    "sub_type": "normal",
    "message_id": -192837465,
    "user_id": 1122334455,
    "raw_message": "明天下午三点在三教开会", //过滤后的消息
    "sender": {
        "user_id": 1122334455,
        "role": "member"
    }
  }
  ```

### 3.3 大模型处理后的私聊日程查询语句
- **事件描述**: `ai_brain` 将处理后的日程查询语句送给 `persistence` 模块进行日程查询。
- **发布方 (Producer)**: `ai_brain`
- **订阅方 (Consumer)**: `persistence`
- **路由坐标**:
  - **Topic / Exchange**: `topic_user_needs`
  - **Tag / RoutingKey**: `private_msg_processed`
- **业务载荷 (Data Payload)**:
  ```json
  {
      "type" : "sql", // or "fun"
      "query" :{
        "sql" : "select * from .... "
      }
  }
  ```

### 3.4 返回给Bot私聊日程
- **事件描述**: `persistence` 将处理后的日程发送给 `bot_gateway` 模块进行发送。
- **发布方 (Producer)**: `persistence`
- **订阅方 (Consumer)**: `bot_gateway`
- **路由坐标**:
  - **Topic / Exchange**: `topic_user_needs`
  - **Tag / RoutingKey**: `private_msg_result`
- **业务载荷 (Data Payload)**:
  ```json
  {
    "type" : "user",
    "user_id": "10111",
    "schedules" :[
      {
        "original_msg_id": -192837465, // 溯源ID
        "schedule_time": [1712401200000, 1712401200000], 
        "location": "三教",            
        "event_content": "开会",       
        "participants": "全体同学",   
        "introduce" : "为了庆祝朱宇翔生日，学校邀请..."
      }
    ]
  }
  ```