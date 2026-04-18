# Memo Echo APIS 接口规范 

**文档摘要**: 本规范定义了 Memo Echo 系统中各个微服务对外部管理端以及内部跨服务协同暴露的 HTTP API 契约。高频业务流转依旧依赖 MQ 与 gRPC，本规范主要服务于**状态控制、数据回传、审计风控与手动补偿**场景。

## 0. 全局规范
* **Content-Type**: `application/json`
* **鉴权 Header**: `Authorization: Bearer <Token>` (除内部调用接口外，管理端接口均需携带)
* **基础响应体 (Base Response)**:
  统一采用泛型 `T` 包装业务数据。执行类指令时 `data` 为 `boolean`。

  ```json
  {
    "code": 200,      // 200-成功, 400-参数校验错误, 401-未授权, 500-服务异常
    "msg": "success", // 提示信息
    "data": true      // 具体业务数据或执行状态 (泛型 T)
  }
  ```

---

## 1. `bot_gateway` 机器人协议网关
**摘要**: 负责对接底层通信框架（如 NapCat/OneBot）。本模块对外暴露管理面板所需的主动控制指令，底层执行采用异步下行管道。

### 1.1 群组管理 (Group Management)

**1. 机器人主动退群 (`leaveFromGroup`)**
* **连接地址**: `POST /api/admin/bot/group/leave`
* **入参**:
  * `id` (Long): 执行操作的机器人 ID
  * `groupId` (Long): 目标群号
  * `message` (String): 退群前发送的临别留言（可为空）

**2. 处理入群邀请 (`handleGroupRequest`)**
* **连接地址**: `POST /api/admin/bot/group/request`
* **入参**:
  * `id` (Long): 执行操作的机器人 ID
  * `flag` (String): 平台下发的请求唯一标识
  * `subType` (String): 请求类型 (`add`-主动加群, `invite`-被邀请)
  * `approve` (Boolean): `true` 同意, `false` 拒绝

### 1.2 社交管理 (Social Management)

**1. 解除好友关系 (`leaveFromFriend`)**
* **连接地址**: `POST /api/admin/bot/friend/leave`
* **入参**:
  * `id` (Long): 执行操作的机器人 ID
  * `friendId` (Long): 需要解除好友的用户 ID

**2. 账号封禁隔离 (`blockUser`)**
* **连接地址**: `POST /api/admin/bot/user/block`
* **入参**:
  * `id` (Long): 执行操作的机器人 ID
  * `userId` (Long): 目标恶意用户 ID

**3. 处理好友申请 (`handleFriendRequest`)**
* **连接地址**: `POST /api/admin/bot/friend/request`
* **入参**:
  * `id` (Long): 执行操作的机器人 ID
  * `flag` (String): 申请事件唯一标识
  * `approve` (Boolean): `true` 同意, `false` 拒绝

---

## 2. `persistence` 持久化与数据中心
**摘要**: 负责核心调度数据的落库、向量库的映射，同时对外提供用户鉴权与风控审计数据支撑。

### 2.1 账户与配置管理 (Account Management)

**1. 用户注册与绑定 (`registerUser`)**
* **连接地址**: `POST /api/user/register`
* **入参**:
  * `account` (String): 登录账号 (或平台绑定的 User ID)
  * `password` (String): 密码 (后端采用 BCrypt 加密存储)
  * `email` (String): 绑定邮箱
  * `phone` (String): 手机号

**2. 更新个人 AI 密钥 (`updateAiConfig`)**
* **连接地址**: `PUT /api/user/config/ai`
* **入参**:
  * `userId` (Long): 用户 ID
  * `aiProvider` (String): 厂商标识 (如 `dashscope`, `openai`)
  * `apiKey` (String): 密钥明文 (落库前进行对称加密)

### 2.2 核心数据同步 (Internal Sync API)
**摘要**: 暴露此接口用于“AI 脑部数据回传”或“手动补偿重试”。

**1. 结构化日程与向量同步 (`syncScheduleData`)**
* **连接地址**: `POST /api/internal/persistence/schedule/sync`
* **介绍**: 接收 `ai_brain` 处理后的完整日程包，通过事务一致性同时写入 MySQL 和 Qdrant。
* **入参**:
  ```json
  {
    "userId": 1122334455,
    "rawMessage": "明天下午三点在三教开会",
    "scheduleDto": {
        "startTime": "2026-04-17 15:00:00",
        "endTime": "2026-04-17 17:00:00",
        "location": "三教",
        "content": "开会",
        "participants": "全体同学"
    },
    "vector": [0.012, -0.054] // 1536维浮点向量
  }
  ```

### 2.3 审计与风控 (Audit API for `control_all`)

**1. 获取敏感词拦截记录 (`getSensitiveLogs`)**
* **连接地址**: `GET /api/admin/persistence/audit/sensitive-logs`
* **介绍**: 给 `control_all` 提供数据支持，用于展示哪些用户在哪些群聊里触发了风控拦截。
* **Query 参数**:
  * `groupId` (Long): 可选，筛选特定群
  * `userId` (Long): 可选，筛选特定用户
  * `page`, `size`: 分页参数
* **响应 `data`**:
  ```json
  {
    "total": 100,
    "list": [
      {
        "logId": "sn-9921",
        "userId": 1122334455,
        "groupId": 987654321,
        "rawContent": "包含敏感词的消息原文",
        "hitKeywords": ["关键词A", "关键词B"],
        "timestamp": 1712345678000
      }
    ]
  }
  ```

---

## 3. `ai_brain` 智能大脑层
**摘要**: 定义大脑如何将自然语言转化为向量数据，并提供开发者调试大模型所需的辅助接口。

### 3.1 语义处理契约 (Internal Brain API)

**1. 消息解析与向量化分发 (`processAndDispatch`)**
* **连接地址**: `POST /api/internal/ai/process`
* **介绍**: 通常由 `ai_brain` 内部监听器触发。将文本转化为向量后，决定后续路由。如果是“查询”则发起 gRPC 检索；如果是“录入”则调用 `/sync` 接口。
* **入参**:
  * `messageId` (Long): 溯源 ID
  * `content` (String): 过滤后的安全文本

### 3.2 调试与运维 (Debug & Ops)

**1. 清理 LLM 上下文记忆 (`clearChatMemory`)**
* **连接地址**: `DELETE /api/admin/ai/memory/{userId}`
* **介绍**: 强制清空指定用户的 `ChatMemory` 上下文历史，防止幻觉堆积。
* **入参** (Path Variable): `userId` (Long)

**2. 模拟 Prompt 链路测试 (`testPromptExtraction`)**
* **连接地址**: `POST /api/admin/ai/test/extract`
* **介绍**: 绕过网关，直接向大脑注入测试文本，验证意图识别和 JSON 格式化输出的稳定性。
* **入参**: `rawMessage` (String)
* **响应 `data`**: 返回 `ScheduleDTO` 的序列化 JSON 结构。

---

## 4. `control_all` 全局控制中心
**摘要**: 充当系统的注册、监控看板与全局调度器，汇总底层数据以提供高维度的决策支撑。

### 4.1 全局调度 (Global Dispatch)

**1. 全服广播推送 (`broadcast`)**
* **连接地址**: `POST /api/admin/control/broadcast`
* **介绍**: 通过 MQ 向所有机器人下发系统维护公告或活动通知。
* **入参**:
  * `content` (String): 广播内容
  * `target` (String): 目标范围 (`all_groups`, `all_users`)

**2. 熔断与恢复控制 (`circuitBreaker`)**
* **连接地址**: `POST /api/admin/control/circuit`
* **介绍**: 紧急暂停指定微服务（如 AI 接口异常时拦截请求）。
* **入参**:
  * `service` (String): 目标微服务 (`ai_brain`, `persistence`)
  * `status` (String): `pause` (挂起), `resume` (恢复)

### 4.2 决策支撑 (Decision Support)

**1. 风险用户画像查询 (`getRiskUserProfile`)**
* **连接地址**: `GET /api/admin/control/risk-profile/{userId}`
* **介绍**: 汇总 `persistence` 提供的敏感记录数据，综合判断并生成该用户的风险画像，为是否调用 `blockUser` 接口提供依据。
* **入参** (Path Variable): `userId` (Longg**
