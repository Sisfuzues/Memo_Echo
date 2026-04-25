<template>
  <main class="dashboard-page">
    <section class="dashboard-card">
      <div class="header-row">
        <div>
          <p class="eyebrow">Dashboard</p>
          <h1>机器人控制台</h1>
          <p class="summary">当前前端已经通过网关访问 bot_gateway 和 persistence。</p>
          <p class="user-line">当前用户ID：{{ currentUserId || '未知' }}</p>
          <p class="user-line">当前身份：{{ isAdmin ? '管理员' : '普通用户' }}</p>
        </div>
        <button class="logout-btn" type="button" @click="handleLogout">退出登录</button>
      </div>

      <div class="toolbar">
        <button class="secondary-btn" type="button" :disabled="isLoadingGroups" @click="loadGroups">
          {{ isLoadingGroups ? '加载中...' : '刷新群列表' }}
        </button>
        <button class="secondary-btn" type="button" :disabled="isLoadingFriends" @click="loadFriends">
          {{ isLoadingFriends ? '加载中...' : '刷新好友列表' }}
        </button>
        <button
          v-if="isAdmin"
          class="secondary-btn"
          type="button"
          :disabled="isLoadingUnsafeGroups"
          @click="loadUnsafeGroups"
        >
          {{ isLoadingUnsafeGroups ? '加载中...' : '刷新风险监控' }}
        </button>
        <button
          v-if="isAdmin"
          class="secondary-btn"
          type="button"
          :disabled="isLoadingBotInfo"
          @click="loadBotInfo"
        >
          {{ isLoadingBotInfo ? '加载中...' : '刷新机器人状态' }}
        </button>
      </div>

      <p v-if="pageMessage" class="feedback" :class="pageMessageIsError ? 'feedback-error' : 'feedback-success'">
        {{ pageMessage }}
      </p>

      <section v-if="isAdmin" class="panel bot-status-panel">
        <div class="panel-head">
          <h2>机器人状态</h2>
          <span class="pill">{{ botStatusList.length }}</span>
        </div>

        <div v-if="isLoadingBotInfo" class="empty-text bot-loading">正在加载机器人状态...</div>

        <div v-else-if="botStatusList.length" class="bot-status-grid">
          <article v-for="bot in botStatusList" :key="bot.rawName" class="bot-status-item">
            <div>
              <strong>{{ bot.name }}</strong>
              <span>{{ bot.rawName }}</span>
            </div>
            <span class="bot-state" :class="botStatusClass(bot.status)">
              {{ botStatusLabel(bot.status) }}
            </span>
          </article>
        </div>

        <p v-else class="empty-text">当前还没有机器人状态数据。</p>
      </section>

      <section v-if="isAdmin" class="panel risk-panel">
        <div class="panel-head">
          <h2>群聊状况监控</h2>
          <span class="pill">{{ monitoredGroups.length }}</span>
        </div>
        <div v-if="monitoredGroups.length" class="table-wrap">
          <table class="monitor-table">
            <thead>
              <tr>
                <th>群名称</th>
                <th>群ID</th>
                <th>成员数</th>
                <th>违规分</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="group in monitoredGroups"
                :key="group.groupId"
                class="clickable-row"
                :class="{ active: selectedUnsafeGroupId === String(group.groupId) }"
                tabindex="0"
                @click="loadUnsafeMessages(group)"
                @keydown.enter.prevent="loadUnsafeMessages(group)"
              >
                <td>{{ group.groupName || `群聊 ${group.groupId}` }}</td>
                <td>{{ group.groupId }}</td>
                <td>{{ group.memberCount ?? '-' }}</td>
                <td>{{ group.score }}</td>
                <td>
                  <span class="risk-badge" :class="riskClass(group.score)">{{ riskLabel(group.score) }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <p v-else class="empty-text">当前还没有群聊监控数据。</p>
      </section>

      <section v-if="isAdmin && selectedUnsafeGroup" class="panel unsafe-detail-panel">
        <div class="panel-head detail-head">
          <div>
            <h2>不安全消息详情</h2>
            <p class="detail-subtitle">
              {{ selectedUnsafeGroup.groupName || `群聊 ${selectedUnsafeGroup.groupId}` }}
              <span>ID: {{ selectedUnsafeGroup.groupId }}</span>
            </p>
          </div>
          <button
            class="secondary-btn"
            type="button"
            :disabled="isLoadingUnsafeMessages"
            @click="loadUnsafeMessages(selectedUnsafeGroup)"
          >
            {{ isLoadingUnsafeMessages ? '加载中...' : '刷新详情' }}
          </button>
        </div>

        <div v-if="isLoadingUnsafeMessages" class="empty-text detail-loading">正在加载不安全消息...</div>

        <div v-else-if="unsafeMessages.length" class="unsafe-message-list">
          <article
            v-for="message in unsafeMessages"
            :key="message.messageId || `${message.groupId}-${message.time}-${message.rawMessage}`"
            class="unsafe-message-item"
          >
            <div class="message-topline">
              <span class="risk-badge" :class="riskClass(message.filterScore)">
                {{ riskLabel(message.filterScore) }}
              </span>
              <span class="message-score">违规分 {{ message.filterScore ?? 0 }}</span>
              <span class="message-time">{{ formatMessageTime(message.time) }}</span>
            </div>
            <p class="message-content">{{ message.rawMessage || '无消息内容' }}</p>
            <dl class="message-meta">
              <div>
                <dt>消息ID</dt>
                <dd>{{ message.messageId || '-' }}</dd>
              </div>
              <div>
                <dt>用户ID</dt>
                <dd>{{ message.userId || message.senderUserId || '-' }}</dd>
              </div>
              <div>
                <dt>群名片</dt>
                <dd>{{ message.senderCard || '-' }}</dd>
              </div>
              <div>
                <dt>昵称</dt>
                <dd>{{ message.senderNickname || '-' }}</dd>
              </div>
              <div>
                <dt>角色</dt>
                <dd>{{ message.senderRole || '-' }}</dd>
              </div>
              <div>
                <dt>消息类型</dt>
                <dd>{{ message.messageType || '-' }}</dd>
              </div>
            </dl>
          </article>
        </div>

        <p v-else class="empty-text">这个群当前没有查到不安全消息明细。</p>
      </section>

      <section class="panel-grid">
        <article class="panel">
          <div class="panel-head">
            <h2>群列表</h2>
            <span class="pill">{{ groups.length }}</span>
          </div>
          <div v-if="groups.length" class="list-wrap">
            <article
              v-for="group in groups"
              :key="group.groupId"
              class="list-item"
              :class="{ active: sendForm.targetType === 'group' && sendForm.targetId === String(group.groupId) }"
              role="button"
              tabindex="0"
              @click="selectGroup(group.groupId)"
              @keydown.enter.prevent="selectGroup(group.groupId)"
            >
              <div class="list-item-main">
                <strong>{{ group.groupName || `群聊 ${group.groupId}` }}</strong>
                <span>ID: {{ group.groupId }}</span>
                <span v-if="group.memberCount">成员数: {{ group.memberCount }}</span>
              </div>
              <div v-if="isAdmin" class="list-item-actions">
                <button
                  class="danger-btn"
                  type="button"
                  :disabled="deletingGroupId === String(group.groupId)"
                  @click.stop="leaveGroup(group)"
                >
                  {{ deletingGroupId === String(group.groupId) ? '处理中...' : '退群' }}
                </button>
              </div>
            </article>
          </div>
          <p v-else class="empty-text">还没有加载到群列表。</p>
        </article>

        <article class="panel">
          <div class="panel-head">
            <h2>好友列表</h2>
            <span class="pill">{{ friends.length }}</span>
          </div>
          <div v-if="friends.length" class="list-wrap">
            <article
              v-for="friend in friends"
              :key="friend.userId"
              class="list-item"
              :class="{ active: sendForm.targetType === 'private' && sendForm.targetId === String(friend.userId) }"
              role="button"
              tabindex="0"
              @click="selectFriend(friend.userId)"
              @keydown.enter.prevent="selectFriend(friend.userId)"
            >
              <div class="list-item-main">
                <strong>{{ friend.nickName || `好友 ${friend.userId}` }}</strong>
                <span>ID: {{ friend.userId }}</span>
                <span v-if="friend.remark">备注: {{ friend.remark }}</span>
              </div>
              <div v-if="isAdmin" class="list-item-actions">
                <button
                  class="danger-btn"
                  type="button"
                  :disabled="deletingFriendId === String(friend.userId)"
                  @click.stop="deleteFriend(friend)"
                >
                  {{ deletingFriendId === String(friend.userId) ? '处理中...' : '删除好友' }}
                </button>
              </div>
            </article>
          </div>
          <p v-else class="empty-text">还没有加载到好友列表。</p>
        </article>
      </section>

      <section class="panel composer">
        <div class="panel-head">
          <h2>发送测试消息</h2>
        </div>

        <div class="target-switch">
          <button
            class="switch-btn"
            :class="{ active: sendForm.targetType === 'group' }"
            type="button"
            @click="sendForm.targetType = 'group'"
          >
            发群消息
          </button>
          <button
            class="switch-btn"
            :class="{ active: sendForm.targetType === 'private' }"
            type="button"
            @click="sendForm.targetType = 'private'"
          >
            发私聊
          </button>
        </div>

        <label class="field">
          <span>{{ sendForm.targetType === 'group' ? '群ID' : '好友ID' }}</span>
          <input v-model.trim="sendForm.targetId" type="text" inputmode="numeric" placeholder="请输入目标 ID" />
        </label>

        <label class="field">
          <span>消息内容</span>
          <textarea
            v-model="sendForm.content"
            rows="4"
            maxlength="500"
            placeholder="输入要发送的测试文本"
          />
        </label>

        <button class="submit-btn" type="button" :disabled="isSending" @click="sendTextMessage">
          {{ isSending ? '发送中...' : '发送消息' }}
        </button>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { apiFetch } from '@/utils/api';
import { clearAuth, getCurrentUserId } from '@/utils/auth';

const router = useRouter();
const currentUserId = computed(() => getCurrentUserId());
const isAdmin = ref(false);

const groups = ref([]);
const friends = ref([]);
const unsafeGroups = ref([]);
const unsafeMessages = ref([]);
const botStatuses = ref({});
const selectedUnsafeGroup = ref(null);
const isLoadingGroups = ref(false);
const isLoadingFriends = ref(false);
const isLoadingUnsafeGroups = ref(false);
const isLoadingUnsafeMessages = ref(false);
const isLoadingBotInfo = ref(false);
const isSending = ref(false);
const deletingGroupId = ref('');
const deletingFriendId = ref('');
const pageMessage = ref('');
const pageMessageIsError = ref(false);

const sendForm = reactive({
  targetType: 'group',
  targetId: '',
  content: ''
});

const monitoredGroups = computed(() => {
  const scoreMap = new Map(unsafeGroups.value.map((item) => [String(item.groupId), Number(item.score || 0)]));
  return [...groups.value]
    .map((group) => ({
      ...group,
      score: scoreMap.get(String(group.groupId)) ?? 0
    }))
    .sort((a, b) => b.score - a.score || (b.memberCount ?? 0) - (a.memberCount ?? 0));
});

const selectedUnsafeGroupId = computed(() =>
  selectedUnsafeGroup.value?.groupId ? String(selectedUnsafeGroup.value.groupId) : ''
);

const botStatusList = computed(() =>
  Object.entries(botStatuses.value)
    .map(([rawName, status]) => ({
      rawName,
      name: rawName.replace(/^bots:QQ:status:/, ''),
      status: Number(status)
    }))
    .sort((a, b) => b.status - a.status || a.name.localeCompare(b.name))
);

const setPageMessage = (message, isError = false) => {
  pageMessage.value = message;
  pageMessageIsError.value = isError;
};

const unwrapApiResponse = async (response, fallbackMessage) => {
  const result = await response.json().catch(() => null);
  if (!response.ok || result?.code !== 200) {
    throw new Error(result?.message || fallbackMessage);
  }
  return result?.data;
};

const readMessageField = (message, camelKey, snakeKey = camelKey) => message?.[camelKey] ?? message?.[snakeKey] ?? null;

const normalizeUnsafeMessage = (message) => ({
  time: readMessageField(message, 'time'),
  postType: readMessageField(message, 'postType', 'post_type'),
  messageType: readMessageField(message, 'messageType', 'message_type'),
  messageId: readMessageField(message, 'messageId', 'message_id'),
  groupId: readMessageField(message, 'groupId', 'group_id'),
  userId: readMessageField(message, 'userId', 'user_id'),
  rawMessage: readMessageField(message, 'rawMessage', 'raw_message'),
  filterScore: Number(readMessageField(message, 'filterScore', 'filter_score') ?? 0),
  senderUserId: readMessageField(message, 'senderUserId', 'sender_user_id'),
  senderRole: readMessageField(message, 'senderRole', 'sender_role'),
  senderCard: readMessageField(message, 'senderCard', 'sender_card'),
  senderNickname: readMessageField(message, 'senderNickname', 'sender_nickname')
});

const formatMessageTime = (time) => {
  const numericTime = Number(time);
  if (!Number.isFinite(numericTime) || numericTime <= 0) {
    return '时间未知';
  }

  const timestamp = numericTime > 1_000_000_000_000 ? numericTime : numericTime * 1000;
  return new Date(timestamp).toLocaleString();
};

const riskLabel = (score) => {
  if (score >= 80) return '高风险';
  if (score >= 30) return '中风险';
  if (score > 0) return '低风险';
  return '正常';
};

const riskClass = (score) => {
  if (score >= 80) return 'risk-high';
  if (score >= 30) return 'risk-medium';
  if (score > 0) return 'risk-low';
  return 'risk-normal';
};

const botStatusLabel = (status) => {
  if (status === 1) return '在线';
  if (status === 0) return '异常';
  return '未知';
};

const botStatusClass = (status) => {
  if (status === 1) return 'bot-online';
  if (status === 0) return 'bot-offline';
  return 'bot-unknown';
};

const loadGroups = async () => {
  isLoadingGroups.value = true;
  try {
    const response = await apiFetch('/api/bot/group/list');
    groups.value = await unwrapApiResponse(response, '获取群列表失败。');
    setPageMessage('群列表已刷新。');
  } catch (error) {
    setPageMessage(error.message || '获取群列表失败。', true);
  } finally {
    isLoadingGroups.value = false;
  }
};

const loadFriends = async () => {
  isLoadingFriends.value = true;
  try {
    const response = await apiFetch('/api/bot/friend/list');
    friends.value = await unwrapApiResponse(response, '获取好友列表失败。');
    setPageMessage('好友列表已刷新。');
  } catch (error) {
    setPageMessage(error.message || '获取好友列表失败。', true);
  } finally {
    isLoadingFriends.value = false;
  }
};

const loadUnsafeGroups = async () => {
  isLoadingUnsafeGroups.value = true;
  try {
    const response = await apiFetch('/api/persistence/admin/group/unsafe');
    unsafeGroups.value = await unwrapApiResponse(response, '获取违规群组列表失败。');
    isAdmin.value = true;
    setPageMessage('风险监控数据已刷新。');
    await loadBotInfo({ silent: true });
  } catch (error) {
    if (error.message === '无管理员权限，禁止访问' || error.message === '未登录或登录状态已失效') {
      isAdmin.value = false;
      unsafeGroups.value = [];
      botStatuses.value = {};
      return;
    }
    setPageMessage(error.message || '获取违规群组列表失败。', true);
  } finally {
    isLoadingUnsafeGroups.value = false;
  }
};

const loadBotInfo = async ({ silent = false } = {}) => {
  isLoadingBotInfo.value = true;
  try {
    const response = await apiFetch('/api/persistence/admin/bot/info');
    const data = await unwrapApiResponse(response, '获取机器人状态失败。');
    botStatuses.value = data && typeof data === 'object' && !Array.isArray(data) ? data : {};
    if (!silent) {
      setPageMessage('机器人状态已刷新。');
    }
  } catch (error) {
    botStatuses.value = {};
    if (!silent) {
      setPageMessage(error.message || '获取机器人状态失败。', true);
    }
  } finally {
    isLoadingBotInfo.value = false;
  }
};

const loadUnsafeMessages = async (group) => {
  const groupId = Number(group?.groupId);
  if (!Number.isInteger(groupId) || groupId <= 0) {
    setPageMessage('群 ID 非法，无法获取详情。', true);
    return;
  }

  selectedUnsafeGroup.value = group;
  isLoadingUnsafeMessages.value = true;
  try {
    const response = await apiFetch(`/api/persistence/admin/group/${groupId}/unsafe/messages`);
    const data = await unwrapApiResponse(response, '获取不安全消息详情失败。');
    unsafeMessages.value = Array.isArray(data) ? data.map(normalizeUnsafeMessage) : [];
    setPageMessage('不安全消息详情已刷新。');
  } catch (error) {
    unsafeMessages.value = [];
    setPageMessage(error.message || '获取不安全消息详情失败。', true);
  } finally {
    isLoadingUnsafeMessages.value = false;
  }
};

const selectGroup = (groupId) => {
  sendForm.targetType = 'group';
  sendForm.targetId = String(groupId);
};

const selectFriend = (userId) => {
  sendForm.targetType = 'private';
  sendForm.targetId = String(userId);
};

const leaveGroup = async (group) => {
  const groupId = Number(group?.groupId);
  if (!Number.isInteger(groupId) || groupId <= 0) {
    setPageMessage('群 ID 非法，无法退群。', true);
    return;
  }

  const groupName = group?.groupName || `群聊 ${groupId}`;
  if (!window.confirm(`确认让机器人退出「${groupName}」吗？`)) {
    return;
  }

  deletingGroupId.value = String(groupId);
  try {
    const response = await apiFetch(`/api/bot/group/${groupId}?isDismiss=false`, {
      method: 'DELETE'
    });
    await unwrapApiResponse(response, '退群失败。');

    groups.value = groups.value.filter((item) => String(item.groupId) !== String(groupId));
    unsafeGroups.value = unsafeGroups.value.filter((item) => String(item.groupId) !== String(groupId));
    if (selectedUnsafeGroupId.value === String(groupId)) {
      selectedUnsafeGroup.value = null;
      unsafeMessages.value = [];
    }
    if (sendForm.targetType === 'group' && sendForm.targetId === String(groupId)) {
      sendForm.targetId = '';
    }
    setPageMessage('机器人已退出该群。');
  } catch (error) {
    setPageMessage(error.message || '退群失败。', true);
  } finally {
    deletingGroupId.value = '';
  }
};

const deleteFriend = async (friend) => {
  const userId = Number(friend?.userId);
  if (!Number.isInteger(userId) || userId <= 0) {
    setPageMessage('好友 ID 非法，无法删除。', true);
    return;
  }

  const friendName = friend?.nickName || friend?.remark || `好友 ${userId}`;
  if (!window.confirm(`确认删除好友「${friendName}」吗？`)) {
    return;
  }

  deletingFriendId.value = String(userId);
  try {
    const response = await apiFetch(`/api/bot/friend/${userId}`, {
      method: 'DELETE'
    });
    await unwrapApiResponse(response, '删除好友失败。');

    friends.value = friends.value.filter((item) => String(item.userId) !== String(userId));
    if (sendForm.targetType === 'private' && sendForm.targetId === String(userId)) {
      sendForm.targetId = '';
    }
    setPageMessage('好友已删除。');
  } catch (error) {
    setPageMessage(error.message || '删除好友失败。', true);
  } finally {
    deletingFriendId.value = '';
  }
};

const sendTextMessage = async () => {
  const numericTargetId = Number(sendForm.targetId);
  const content = sendForm.content.trim();

  if (!Number.isInteger(numericTargetId) || numericTargetId <= 0) {
    setPageMessage('目标 ID 必须是正整数。', true);
    return;
  }

  if (!content) {
    setPageMessage('消息内容不能为空。', true);
    return;
  }

  isSending.value = true;
  try {
    const payload =
      sendForm.targetType === 'group'
        ? { groupId: numericTargetId, content }
        : { privateId: numericTargetId, content };

    const response = await apiFetch('/api/bot/send/text', {
      method: 'POST',
      json: payload
    });

    await unwrapApiResponse(response, '发送消息失败。');
    setPageMessage(sendForm.targetType === 'group' ? '群消息已发送。' : '私聊消息已发送。');
    sendForm.content = '';
  } catch (error) {
    setPageMessage(error.message || '发送消息失败。', true);
  } finally {
    isSending.value = false;
  }
};

const handleLogout = async () => {
  clearAuth();
  await router.push('/');
};

onMounted(async () => {
  await Promise.all([loadGroups(), loadFriends()]);
  await loadUnsafeGroups();
});
</script>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  padding: 24px;
}

.dashboard-card {
  width: min(100%, 1120px);
  margin: 0 auto;
  padding: 32px;
  border-radius: 28px;
  background: rgba(255, 250, 243, 0.88);
  border: 1px solid rgba(138, 109, 59, 0.12);
  box-shadow: 0 24px 80px rgba(71, 46, 16, 0.14);
  backdrop-filter: blur(18px);
}

.header-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-start;
}

.eyebrow {
  color: var(--auth-accent-deep);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.78rem;
  font-weight: 700;
}

h1 {
  margin-top: 10px;
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 700;
}

.summary,
.user-line {
  margin-top: 12px;
  color: var(--auth-text-secondary);
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.panel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.panel {
  padding: 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.62);
  border: 1px solid rgba(118, 89, 43, 0.12);
}

.bot-status-panel {
  margin-top: 24px;
}

.risk-panel {
  margin-top: 24px;
}

.unsafe-detail-panel {
  margin-top: 20px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.panel-head h2 {
  font-size: 1.2rem;
  font-weight: 700;
}

.pill {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(214, 111, 42, 0.12);
  color: var(--auth-accent-deep);
  font-size: 0.85rem;
  font-weight: 700;
}

.table-wrap {
  margin-top: 16px;
  overflow: auto;
}

.monitor-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 680px;
}

.monitor-table th,
.monitor-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(118, 89, 43, 0.12);
  text-align: left;
  color: var(--auth-text-primary);
}

.monitor-table th {
  color: var(--auth-text-secondary);
  font-size: 0.9rem;
}

.clickable-row {
  cursor: pointer;
  transition: background 0.18s ease, box-shadow 0.18s ease;
}

.clickable-row:hover,
.clickable-row:focus {
  background: rgba(214, 111, 42, 0.07);
  outline: none;
}

.clickable-row.active {
  background: rgba(255, 159, 28, 0.12);
  box-shadow: inset 4px 0 0 var(--auth-accent-strong);
}

.risk-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 72px;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 0.82rem;
  font-weight: 700;
}

.risk-normal {
  background: rgba(66, 125, 78, 0.12);
  color: #255834;
}

.risk-low {
  background: rgba(214, 111, 42, 0.12);
  color: #8d4f1d;
}

.risk-medium {
  background: rgba(208, 131, 32, 0.16);
  color: #8a5800;
}

.risk-high {
  background: rgba(176, 62, 47, 0.14);
  color: #8b2718;
}

.bot-loading {
  margin-top: 16px;
}

.bot-status-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.bot-status-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  min-width: 0;
  padding: 14px 16px;
  border: 1px solid rgba(118, 89, 43, 0.12);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.82);
}

.bot-status-item div {
  min-width: 0;
  display: grid;
  gap: 3px;
}

.bot-status-item strong {
  color: var(--auth-text-primary);
  font-weight: 700;
  overflow-wrap: anywhere;
}

.bot-status-item span {
  color: var(--auth-text-secondary);
  font-size: 0.86rem;
  overflow-wrap: anywhere;
}

.bot-state {
  flex: 0 0 auto;
  min-width: 64px;
  padding: 6px 10px;
  border-radius: 999px;
  text-align: center;
  font-size: 0.82rem;
  font-weight: 700;
}

.bot-online {
  background: rgba(66, 125, 78, 0.12);
  color: #255834;
}

.bot-offline {
  background: rgba(176, 62, 47, 0.12);
  color: #8b2718;
}

.bot-unknown {
  background: rgba(111, 91, 73, 0.12);
  color: var(--auth-text-secondary);
}

.detail-head {
  align-items: flex-start;
}

.detail-subtitle {
  margin-top: 6px;
  color: var(--auth-text-secondary);
}

.detail-subtitle span {
  margin-left: 10px;
}

.detail-loading {
  margin-top: 16px;
}

.unsafe-message-list {
  display: grid;
  gap: 14px;
  margin-top: 16px;
  max-height: 560px;
  overflow: auto;
  padding-right: 4px;
}

.unsafe-message-item {
  padding: 16px;
  border: 1px solid rgba(118, 89, 43, 0.12);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.82);
}

.message-topline {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.message-score,
.message-time {
  color: var(--auth-text-secondary);
  font-size: 0.9rem;
}

.message-content {
  margin-top: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(47, 34, 23, 0.05);
  color: var(--auth-text-primary);
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.message-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.message-meta div {
  min-width: 0;
}

.message-meta dt {
  color: var(--auth-text-secondary);
  font-size: 0.82rem;
}

.message-meta dd {
  margin-top: 4px;
  color: var(--auth-text-primary);
  font-weight: 600;
  overflow-wrap: anywhere;
}

.list-wrap {
  display: grid;
  gap: 10px;
  margin-top: 16px;
  max-height: 320px;
  overflow: auto;
}

.list-item {
  text-align: left;
  border: 1px solid rgba(118, 89, 43, 0.12);
  border-radius: 16px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.list-item-main {
  display: grid;
  gap: 4px;
  min-width: 0;
}

.list-item-main strong {
  color: var(--auth-text-primary);
  overflow-wrap: anywhere;
}

.list-item-main span,
.empty-text {
  color: var(--auth-text-secondary);
  overflow-wrap: anywhere;
}

.list-item.active {
  border-color: rgba(214, 111, 42, 0.56);
  box-shadow: 0 0 0 3px rgba(255, 159, 28, 0.14);
}

.list-item-actions {
  flex: 0 0 auto;
}

.danger-btn {
  border: 0;
  border-radius: 14px;
  padding: 9px 12px;
  background: rgba(176, 62, 47, 0.12);
  color: #8b2718;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.danger-btn:disabled {
  opacity: 0.65;
  cursor: wait;
}

.composer {
  margin-top: 24px;
}

.target-switch {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.switch-btn,
.secondary-btn,
.submit-btn,
.logout-btn {
  border: 0;
  border-radius: 16px;
  padding: 12px 16px;
  font-weight: 700;
  cursor: pointer;
}

.switch-btn,
.secondary-btn {
  background: rgba(214, 111, 42, 0.1);
  color: var(--auth-accent-deep);
}

.switch-btn.active {
  background: linear-gradient(135deg, var(--auth-accent), var(--auth-accent-strong));
  color: #1d1204;
}

.field {
  display: block;
  margin-top: 18px;
}

.field span {
  display: block;
  margin-bottom: 8px;
  color: var(--auth-text-primary);
  font-weight: 600;
}

input,
textarea {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid rgba(118, 89, 43, 0.14);
  border-radius: 16px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.92);
  color: var(--auth-text-primary);
}

textarea {
  resize: vertical;
}

.submit-btn,
.logout-btn {
  margin-top: 20px;
  background: linear-gradient(135deg, var(--auth-accent), var(--auth-accent-strong));
  color: #1d1204;
  box-shadow: 0 16px 32px rgba(214, 111, 42, 0.28);
}

.feedback {
  margin-top: 18px;
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 0.95rem;
}

.feedback-success {
  background: rgba(66, 125, 78, 0.12);
  color: #255834;
}

.feedback-error {
  background: rgba(176, 62, 47, 0.12);
  color: #8b2718;
}

@media (max-width: 900px) {
  .panel-grid {
    grid-template-columns: 1fr;
  }

  .header-row {
    flex-direction: column;
  }

  .bot-status-grid {
    grid-template-columns: 1fr;
  }

  .detail-head {
    flex-direction: column;
  }

  .message-meta {
    grid-template-columns: 1fr;
  }

  .list-item {
    align-items: stretch;
    flex-direction: column;
  }

  .list-item-actions {
    display: flex;
  }

  .danger-btn {
    width: 100%;
  }
}
</style>
