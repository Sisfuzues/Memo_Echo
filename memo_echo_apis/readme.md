# APIS
## 全局规范
- **Content-Type**: `application/json`
- **基础响应体**:
  ```json
  {
    "code": 200,      // 200-成功, 4xx-请求错误, 5xx-信道异常
    "msg": "string",  // 提示信息
    "data": "boolean" // 指令下发执行状态
  }
## bot_gateway 机器人板块
### **摘要**: 

因为机器人和QQ的链接通过websocket，然后读取的消息存入到MQ中，所以不需要给出一个外部读信息的接口。

本规范定义了 `bot-gateway` 向外部管理端（如控制台或管理面板）提供的控制指令接口。所有接口均通过内部 WebSocket 管道下行指令至底层通信层（NapCat），采用异步下行、同步反馈模式。

--- 
### 一. 群组管理
1. **leaveFromGroup**
- 介绍：要求指定机器人退出指定群聊
- 连接地址： POST /api/admin/bot/group/leave
- 入参：
    - `id` 类型 Long , 指的是需要退群的机器人ID。
    - `groupId` 类型 Long , 指的需要退的群ID。
    - `message` 类型 String , 指的是退群前需要发送的消息。

2. **addToGroup**
- 介绍: 指定机器人同意指定的入群申请群聊
- 连接地址： POST /api/admin/bot/group/add
- 入参：
    - `id` 类型 Long , 指的是需要入群的机器人ID。
    - `flag` 类型 String , 指的需要入群的邀请的事件flag。
    - `subType` 类型 String , 指的是加群类型
### 二. 社交管理
1. **leaveFromFriend**
- 介绍: 让指定的机器人与指定用户解除好友
- 连接地址： POST /api/admin/bot/friend/leave
- 入参：
    - `id` 类型 Long , 指的是需要操作的机器人ID。
    - `friendId` 类型 Long , 指需要解除好友的用户ID。
2. **blockUser**
- 介绍: 让指定的机器人拉黑指定用户
- 连接地址： POST /api/admin/bot/user/block
- 入参：
    - `id` 类型 Long , 指的是需要操作的机器人ID。
    - `userId` 类型 Long , 指需要拉黑的用户ID。
- 出参：
    - bool 类型的参数，表示是否成功拉黑。
3. **addFriend**
- 介绍: 同意指定用户的好友申请
- 连接地址： POST /api/admin/bot/friend/add
- 入参：
    - `id` 类型 Long , 指的是需要操作的机器人ID。
    - `flag` 类型 Long , 指需要同意是否同意好友申请的 flag
- 出参：
    - bool 类型的参数，表示是否成功添加。
## control_all

## persistence
## sensitive_filter
## ai_brain