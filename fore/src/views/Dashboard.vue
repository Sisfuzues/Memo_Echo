<template>
  <main class="dashboard-page" :class="{ 'theme-light': isLightTheme }">
    <aside class="dashboard-sidebar">
      <div class="sidebar-brand">
        <span class="brand-mark">ME</span>
        <div>
          <strong>Memo Echo</strong>
          <small>{{ isAdmin ? 'Admin workspace' : 'User workspace' }}</small>
        </div>
      </div>

      <div class="sidebar-user">
        <span>用户 {{ currentUserId || '未知' }}</span>
        <strong>{{ isAdmin ? '管理员' : '普通用户' }}</strong>
      </div>

      <nav v-if="isAdmin" class="sidebar-nav" aria-label="管理员功能分类">
        <button
          v-for="tab in adminTabs"
          :key="tab.key"
          class="nav-btn"
          :class="{ active: activeAdminTab === tab.key }"
          type="button"
          @click="activeAdminTab = tab.key"
        >
          <span>{{ tab.label }}</span>
        </button>
      </nav>

      <div class="settings-menu" :class="{ open: showSettings }">
        <button class="settings-btn" type="button" @click="showSettings = !showSettings">
          <span>设置</span>
          <strong>{{ showSettings ? '收起' : '打开' }}</strong>
        </button>

        <div v-if="showSettings" class="settings-panel">
          <button class="setting-row" type="button" @click="toggleTheme">
            <span>主题模式</span>
            <strong>{{ isLightTheme ? '浅色' : '深色' }}</strong>
          </button>
        </div>
      </div>
      <button class="logout-btn" type="button" @click="handleLogout">退出登录</button>
    </aside>

    <section class="dashboard-main">
      <div class="header-row">
        <div>
          <h1>{{ isAdmin ? currentAdminTabTitle : '群日程托管申请' }}</h1>
          <p class="summary">{{ isAdmin ? currentAdminTabDescription : '提交需要机器人托管日程的群聊，等待管理员审核。' }}</p>
        </div>

        <div v-if="isAdmin" class="toolbar">
          <button
            v-if="activeAdminTab === 'directory'"
            class="secondary-btn"
            type="button"
            :disabled="isLoadingGroups"
            @click="loadGroups"
          >
            {{ isLoadingGroups ? '加载中...' : '刷新群列表' }}
          </button>
          <button
            v-if="activeAdminTab === 'directory'"
            class="secondary-btn"
            type="button"
            :disabled="isLoadingFriends"
            @click="loadFriends"
          >
            {{ isLoadingFriends ? '加载中...' : '刷新好友列表' }}
          </button>
          <button
            v-if="activeAdminTab === 'requests'"
            class="secondary-btn"
            type="button"
            :disabled="isLoadingScheduleRequests"
            @click="loadScheduleRequests"
          >
            {{ isLoadingScheduleRequests ? '加载中...' : '刷新日程申请' }}
          </button>
          <button
            v-if="activeAdminTab === 'risk'"
            class="secondary-btn"
            type="button"
            :disabled="isLoadingUnsafeGroups"
            @click="loadUnsafeGroups"
          >
            {{ isLoadingUnsafeGroups ? '加载中...' : '刷新风险监控' }}
          </button>
          <button
            v-if="activeAdminTab === 'directory'"
            class="secondary-btn"
            type="button"
            :disabled="isLoadingBotInfo"
            @click="loadBotInfo"
          >
            {{ isLoadingBotInfo ? '加载中...' : '刷新机器人状态' }}
          </button>
        </div>
      </div>

      <p v-if="pageMessage" class="feedback" :class="pageMessageIsError ? 'feedback-error' : 'feedback-success'">
        {{ pageMessage }}
      </p>

      <section v-if="isAdmin && activeAdminTab === 'directory'" class="panel bot-status-panel tab-panel">
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

      <section v-if="!isAdmin" class="panel user-home-panel">
        <div class="panel-head">
          <h2>群日程托管申请</h2>
        </div>
        <p class="empty-text">普通用户可提交群日程托管申请，管理员审核通过后机器人会加入对应群聊。</p>
      </section>

      <section v-if="!isAdmin || activeAdminTab === 'requests'" class="panel schedule-request-panel tab-panel">
        <div class="panel-head">
          <h2>申请托管群日程</h2>
        </div>

        <div class="request-form-grid">
          <label class="field compact-field">
            <span>群号</span>
            <input
              v-model.trim="scheduleRequestForm.groupId"
              type="text"
              inputmode="numeric"
              placeholder="请输入需要管理日程的群号"
            />
          </label>

          <label class="field compact-field">
            <span>原因</span>
            <input
              v-model.trim="scheduleRequestForm.reason"
              type="text"
              maxlength="200"
              placeholder="可选，例如课程群需要自动提醒作业"
            />
          </label>

          <button class="submit-btn inline-submit" type="button" :disabled="isSubmittingScheduleRequest" @click="submitScheduleRequest">
            {{ isSubmittingScheduleRequest ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </section>

      <section v-if="isAdmin && activeAdminTab === 'requests'" class="panel schedule-admin-panel">
        <div class="panel-head">
          <h2>群日程管理申请</h2>
          <span class="pill">{{ scheduleRequests.length }}</span>
        </div>

        <div v-if="isLoadingScheduleRequests" class="empty-text request-loading">正在加载日程申请...</div>

        <div v-else-if="scheduleRequests.length" class="table-wrap">
          <table class="monitor-table request-table">
            <thead>
              <tr>
                <th>群号</th>
                <th>申请用户</th>
                <th>原因</th>
                <th>提交时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="request in scheduleRequests" :key="request.groupId">
                <td>{{ request.groupId }}</td>
                <td>{{ request.requesterUserId || '-' }}</td>
                <td>{{ request.reason || '未填写' }}</td>
                <td>{{ formatRequestTime(request.createdAt) }}</td>
                <td>
                  <div class="table-actions">
                    <button
                      class="secondary-btn table-action-btn"
                      type="button"
                      :disabled="processingGroupRequestId === String(request.groupId)"
                      @click="approveScheduleRequest(request)"
                    >
                      {{ processingGroupRequestId === String(request.groupId) ? '处理中...' : '同意' }}
                    </button>
                    <button
                      class="danger-btn table-action-btn"
                      type="button"
                      :disabled="processingGroupRequestId === String(request.groupId)"
                      @click="rejectScheduleRequest(request)"
                    >
                      拒绝
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="empty-state">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 6.5A2.5 2.5 0 0 1 6.5 4h11A2.5 2.5 0 0 1 20 6.5v11a2.5 2.5 0 0 1-2.5 2.5h-11A2.5 2.5 0 0 1 4 17.5v-11Z" />
            <path d="M4.5 14h4l1.6 2h3.8l1.6-2h4" />
          </svg>
          <p>当前还没有群日程管理申请</p>
        </div>
      </section>

      <section v-if="isAdmin && activeAdminTab === 'risk'" class="panel risk-panel tab-panel">
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

      <section v-if="isAdmin && activeAdminTab === 'risk' && selectedUnsafeGroup" class="panel unsafe-detail-panel">
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

      <section v-if="isAdmin && activeAdminTab === 'directory'" class="panel-grid tab-panel">
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

      <section v-if="isAdmin && activeAdminTab === 'messaging'" class="panel composer tab-panel">
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
const activeAdminTab = ref('requests');

const adminTabs = [
  { key: 'requests', label: '日程申请', title: '日程申请', description: '提交新的托管申请，并处理用户提交的待办。' },
  { key: 'directory', label: '群与机器人', title: '群与机器人', description: '查看机器人状态、群聊列表和好友列表。' },
  { key: 'risk', label: '风险监控', title: '风险监控', description: '查看群聊违规分和不安全消息详情。' },
  { key: 'messaging', label: '发送消息', title: '发送消息', description: '向指定群聊或好友发送测试消息。' }
];

const currentAdminTab = computed(() =>
  adminTabs.find((tab) => tab.key === activeAdminTab.value) ?? adminTabs[0]
);
const currentAdminTabLabel = computed(() => currentAdminTab.value.label);
const currentAdminTabTitle = computed(() => currentAdminTab.value.title);
const currentAdminTabDescription = computed(() => currentAdminTab.value.description);

const groups = ref([]);
const friends = ref([]);
const unsafeGroups = ref([]);
const unsafeMessages = ref([]);
const botStatuses = ref({});
const scheduleRequests = ref([]);
const selectedUnsafeGroup = ref(null);
const isLoadingGroups = ref(false);
const isLoadingFriends = ref(false);
const isLoadingUnsafeGroups = ref(false);
const isLoadingUnsafeMessages = ref(false);
const isLoadingBotInfo = ref(false);
const isLoadingScheduleRequests = ref(false);
const isSending = ref(false);
const isSubmittingScheduleRequest = ref(false);
const processingGroupRequestId = ref('');
const deletingGroupId = ref('');
const deletingFriendId = ref('');
const pageMessage = ref('');
const pageMessageIsError = ref(false);
const showSettings = ref(false);
const isLightTheme = ref(false);
let pageMessageTimer = null;

const sendForm = reactive({
  targetType: 'group',
  targetId: '',
  content: ''
});

const scheduleRequestForm = reactive({
  groupId: '',
  reason: ''
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
  if (pageMessageTimer) {
    window.clearTimeout(pageMessageTimer);
  }

  pageMessage.value = message;
  pageMessageIsError.value = isError;
  pageMessageTimer = window.setTimeout(() => {
    pageMessage.value = '';
    pageMessageIsError.value = false;
    pageMessageTimer = null;
  }, isError ? 6500 : 3200);
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

const formatRequestTime = (time) => {
  const numericTime = Number(time);
  if (!Number.isFinite(numericTime) || numericTime <= 0) {
    return '时间未知';
  }

  return new Date(numericTime).toLocaleString();
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

const loadUnsafeGroups = async ({ silent = false } = {}) => {
  isLoadingUnsafeGroups.value = true;
  try {
    const response = await apiFetch('/api/persistence/admin/group/unsafe');
    unsafeGroups.value = await unwrapApiResponse(response, '获取违规群组列表失败。');
    isAdmin.value = true;
    if (!silent) {
      setPageMessage('风险监控数据已刷新。');
    }
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

const loadScheduleRequests = async ({ silent = false } = {}) => {
  isLoadingScheduleRequests.value = true;
  try {
    const response = await apiFetch('/api/persistence/admin/schedule/group/requests');
    const data = await unwrapApiResponse(response, '获取群日程申请失败。');
    scheduleRequests.value = Array.isArray(data) ? data : [];
    if (!silent) {
      setPageMessage('群日程申请已刷新。');
    }
  } catch (error) {
    scheduleRequests.value = [];
    if (!silent) {
      setPageMessage(error.message || '获取群日程申请失败。', true);
    }
  } finally {
    isLoadingScheduleRequests.value = false;
  }
};

const submitScheduleRequest = async () => {
  const groupId = Number(scheduleRequestForm.groupId);
  const reason = scheduleRequestForm.reason.trim();

  if (!Number.isInteger(groupId) || groupId <= 0) {
    setPageMessage('群号必须是正整数。', true);
    return;
  }

  if (reason.length > 200) {
    setPageMessage('原因最多 200 个字符。', true);
    return;
  }

  isSubmittingScheduleRequest.value = true;
  try {
    const response = await apiFetch('/api/persistence/userops/schedule/group/request', {
      method: 'POST',
      json: { groupId, reason }
    });
    const request = await unwrapApiResponse(response, '提交群日程申请失败。');
    setPageMessage('群日程管理申请已提交。');
    scheduleRequestForm.groupId = '';
    scheduleRequestForm.reason = '';

    if (isAdmin.value && request) {
      scheduleRequests.value = [
        request,
        ...scheduleRequests.value.filter((item) => String(item.groupId) !== String(request.groupId))
      ];
    }
  } catch (error) {
    setPageMessage(error.message || '提交群日程申请失败。', true);
  } finally {
    isSubmittingScheduleRequest.value = false;
  }
};

const removeScheduleRequestFromPending = async (groupId) => {
  const response = await apiFetch(`/api/persistence/admin/schedule/group/requests/${groupId}`, {
    method: 'DELETE'
  });
  await unwrapApiResponse(response, '更新申请状态失败。');
  scheduleRequests.value = scheduleRequests.value.filter((item) => String(item.groupId) !== String(groupId));
};

const approveScheduleRequest = async (request) => {
  const groupId = Number(request?.groupId);
  if (!Number.isInteger(groupId) || groupId <= 0) {
    setPageMessage('群号非法，无法处理。', true);
    return;
  }

  processingGroupRequestId.value = String(groupId);
  try {
    const response = await apiFetch('/api/bot/group/request', {
      method: 'POST',
      json: {
        groupId,
        approve: true,
        subType: 'invite',
        reason: request?.reason || ''
      }
    });
    const data = await unwrapApiResponse(response, '处理群日程申请失败。');
    await removeScheduleRequestFromPending(groupId);
    setPageMessage(data?.message || '申请已同意并移出待处理列表。');
    await loadGroups();
  } catch (error) {
    setPageMessage(error.message || '同意申请失败。', true);
  } finally {
    processingGroupRequestId.value = '';
  }
};

const rejectScheduleRequest = async (request) => {
  const groupId = Number(request?.groupId);
  if (!Number.isInteger(groupId) || groupId <= 0) {
    setPageMessage('群号非法，无法拒绝。', true);
    return;
  }

  const reason = window.prompt('请输入拒绝理由（可留空）：', '');
  if (reason === null) {
    return;
  }

  const trimmedReason = reason.trim();
  if (trimmedReason.length > 200) {
    setPageMessage('拒绝理由最多 200 个字符。', true);
    return;
  }

  processingGroupRequestId.value = String(groupId);
  try {
    const response = await apiFetch(`/api/persistence/admin/schedule/group/requests/${groupId}/reject`, {
      method: 'POST',
      json: { reason: trimmedReason }
    });
    const data = await unwrapApiResponse(response, '拒绝申请失败。');
    scheduleRequests.value = scheduleRequests.value.filter((item) => String(item.groupId) !== String(groupId));
    setPageMessage(data || '申请已拒绝并移出待处理列表。');
  } catch (error) {
    setPageMessage(error.message || '拒绝申请失败。', true);
  } finally {
    processingGroupRequestId.value = '';
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

const toggleTheme = () => {
  isLightTheme.value = !isLightTheme.value;
};

onMounted(async () => {
  await loadUnsafeGroups({ silent: true });
  if (isAdmin.value) {
    await Promise.all([loadGroups(), loadFriends(), loadScheduleRequests({ silent: true })]);
  }
});
</script>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 248px minmax(0, 1fr);
  background: #0a0a0a;
  color: #f4f4f5;
}

.dashboard-sidebar {
  min-height: 100vh;
  padding: 18px 14px;
  background: #0f1015;
  color: #f7f7fb;
  border-right: 1px solid #262833;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 8px 16px;
  border-bottom: 1px solid #262833;
}

.brand-mark {
  display: inline-grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: 8px;
  background: linear-gradient(135deg, #6c5ce7, #3b82f6);
  color: #fff;
  font-size: 0.78rem;
  font-weight: 800;
}

.sidebar-brand strong {
  display: block;
  color: #fff;
  font-weight: 760;
}

.sidebar-brand small,
.sidebar-user span {
  display: block;
  color: #9699a8;
  font-size: 0.82rem;
}

.sidebar-user {
  padding: 10px 12px;
  border: 1px solid #262833;
  border-radius: 8px;
  background: #171923;
}

.sidebar-user strong {
  display: block;
  margin-top: 4px;
  color: #f7f7fb;
  font-weight: 720;
}

.sidebar-nav {
  display: grid;
  gap: 6px;
}

.nav-btn {
  width: 100%;
  min-height: 38px;
  padding: 9px 10px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #b9bdca;
  text-align: left;
  cursor: pointer;
}

.nav-btn span {
  color: inherit;
  font-weight: 650;
  white-space: nowrap;
}

.nav-btn:hover {
  background: #1b1d28;
  color: #fff;
}

.nav-btn.active {
  background: #232635;
  color: #fff;
  box-shadow: inset 3px 0 0 #6c5ce7;
}

.dashboard-main {
  min-width: 0;
  height: 100vh;
  overflow: auto;
  padding: 28px 32px;
  background: #0a0a0a;
}

.header-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: center;
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

h1 {
  font-size: clamp(1.9rem, 3vw, 2.55rem);
  line-height: 1.12;
  font-weight: 760;
  color: #f4f4f5;
}

.summary,
.user-line {
  margin-top: 8px;
  color: #a1a1aa;
}

.toolbar {
  display: flex;
  gap: 12px;
  min-height: 38px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
}

.panel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  margin-top: 16px;
}

.panel {
  padding: 20px;
  border-radius: 10px;
  background: #1c1c1e;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 18px 60px rgba(0, 0, 0, 0.22);
}

.bot-status-panel {
  margin-top: 16px;
}

.risk-panel {
  margin-top: 16px;
}

.schedule-request-panel,
.schedule-admin-panel {
  margin-top: 16px;
}

.unsafe-detail-panel {
  margin-top: 16px;
}

.tab-panel {
  margin-top: 16px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.panel-head h2 {
  font-size: 1.05rem;
  font-weight: 760;
  color: #f4f4f5;
}

.pill {
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  color: #d4d4d8;
  font-size: 0.85rem;
  font-weight: 700;
}

.table-wrap {
  margin-top: 16px;
  overflow: auto;
  max-height: calc(100vh - 315px);
}

.request-form-grid {
  display: grid;
  grid-template-columns: minmax(180px, 0.7fr) minmax(240px, 1.3fr) auto;
  gap: 14px;
  align-items: end;
  margin-top: 8px;
}

.compact-field {
  margin-top: 0;
}

.inline-submit {
  margin-top: 0;
  min-width: 116px;
  height: 46px;
}

.request-loading {
  margin-top: 16px;
}

.request-table {
  min-width: 840px;
}

.table-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.table-action-btn {
  padding: 8px 12px;
  white-space: nowrap;
}

.monitor-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 680px;
}

.monitor-table th,
.monitor-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  text-align: left;
  color: #f4f4f5;
}

.monitor-table th {
  color: #a1a1aa;
  font-size: 0.9rem;
}

.clickable-row {
  cursor: pointer;
  transition: background 0.18s ease, box-shadow 0.18s ease;
}

.clickable-row:hover,
.clickable-row:focus {
  background: rgba(255, 255, 255, 0.04);
  outline: none;
}

.clickable-row.active {
  background: rgba(108, 92, 231, 0.16);
  box-shadow: inset 3px 0 0 #6c5ce7;
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
  background: rgba(34, 197, 94, 0.1);
  color: #4ade80;
}

.risk-low {
  background: rgba(47, 128, 237, 0.1);
  color: #60a5fa;
}

.risk-medium {
  background: rgba(245, 158, 11, 0.12);
  color: #fbbf24;
}

.risk-high {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
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
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.035);
}

.bot-status-item div {
  min-width: 0;
  display: grid;
  gap: 3px;
}

.bot-status-item strong {
  color: #f4f4f5;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.bot-status-item span {
  color: #71717a;
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
  background: rgba(34, 197, 94, 0.1);
  border: 1px solid rgba(34, 197, 94, 0.18);
  color: #4ade80;
}

.bot-offline {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.18);
  color: #f87171;
}

.bot-unknown {
  background: rgba(111, 91, 73, 0.12);
  color: #a1a1aa;
}

.detail-head {
  align-items: flex-start;
}

.detail-subtitle {
  margin-top: 6px;
  color: #a1a1aa;
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
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.035);
}

.message-topline {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.message-score,
.message-time {
  color: #a1a1aa;
  font-size: 0.9rem;
}

.message-content {
  margin-top: 12px;
  padding: 12px 14px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.055);
  color: #f4f4f5;
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
  color: #a1a1aa;
  font-size: 0.82rem;
}

.message-meta dd {
  margin-top: 4px;
  color: #f4f4f5;
  font-weight: 600;
  overflow-wrap: anywhere;
}

.list-wrap {
  display: grid;
  gap: 10px;
  margin-top: 16px;
  max-height: calc(100vh - 360px);
  overflow: auto;
}

.list-item {
  text-align: left;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.035);
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
  color: #f4f4f5;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.list-item-main span {
  color: #71717a;
  overflow-wrap: anywhere;
}

.empty-text {
  color: #a1a1aa;
  overflow-wrap: anywhere;
}

.list-item.active {
  border-color: rgba(108, 92, 231, 0.48);
  box-shadow: 0 0 0 3px rgba(108, 92, 231, 0.10);
}

.list-item-actions {
  flex: 0 0 auto;
}

.danger-btn {
  border: 1px solid rgba(239, 68, 68, 0.18);
  border-radius: 8px;
  padding: 9px 12px;
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
}

.danger-btn:hover:not(:disabled) {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(248, 113, 113, 0.32);
  box-shadow: 0 0 22px rgba(239, 68, 68, 0.1);
}

.danger-btn:disabled {
  opacity: 0.65;
  cursor: wait;
}

.composer {
  margin-top: 16px;
}

.target-switch {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.switch-btn,
.secondary-btn,
.submit-btn,
.settings-btn,
.setting-row,
.logout-btn {
  min-height: 36px;
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 9px 13px;
  font-weight: 650;
  cursor: pointer;
  line-height: 1.2;
  text-align: center;
  white-space: nowrap;
  color: inherit;
}

.switch-btn *,
.secondary-btn *,
.submit-btn *,
.settings-btn *,
.setting-row *,
.logout-btn *,
.danger-btn * {
  color: inherit;
}

.switch-btn,
.secondary-btn {
  background: rgba(255, 255, 255, 0.055);
  border-color: rgba(255, 255, 255, 0.12);
  color: #e4e4e7;
}

.switch-btn:disabled,
.secondary-btn:disabled,
.submit-btn:disabled,
.settings-btn:disabled,
.setting-row:disabled,
.logout-btn:disabled,
.danger-btn:disabled {
  opacity: 0.72;
  cursor: wait;
}

.switch-btn.active {
  background: #6c5ce7;
  border-color: #6c5ce7;
  color: #fff;
}

.field {
  display: block;
  margin-top: 18px;
}

.field span {
  display: block;
  margin-bottom: 8px;
  color: #d4d4d8;
  font-weight: 600;
}

input,
textarea {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 8px;
  padding: 14px 16px;
  background: #111113;
  color: #f4f4f5;
}

textarea {
  resize: vertical;
}

.submit-btn,
.logout-btn {
  margin-top: 20px;
  background: #6c5ce7;
  border-color: #6c5ce7;
  color: #fff;
  box-shadow: none;
}

.settings-menu {
  width: 100%;
  margin-top: auto;
  display: grid;
  gap: 8px;
}

.settings-btn,
.setting-row {
  width: 100%;
  background: rgba(255, 255, 255, 0.055);
  border-color: rgba(255, 255, 255, 0.12);
  color: #e4e4e7;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.settings-btn:hover,
.setting-row:hover {
  background: rgba(255, 255, 255, 0.09);
  border-color: rgba(255, 255, 255, 0.18);
}

.settings-btn span,
.setting-row span {
  font-size: 0.88rem;
  color: #a1a1aa;
}

.settings-btn strong,
.setting-row strong {
  font-size: 0.92rem;
  font-weight: 700;
}

.settings-panel {
  padding: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.035);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.16);
}

.setting-row {
  min-height: 38px;
}

.logout-btn {
  flex: 0 0 auto;
  width: 100%;
  margin-top: 10px;
}

.request-form-grid input {
  height: 46px;
}

.empty-state {
  display: grid;
  place-items: center;
  gap: 14px;
  padding: 64px 20px;
  color: #a1a1aa;
  text-align: center;
}

.empty-state svg {
  width: 46px;
  height: 46px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.4;
  opacity: 0.72;
}

.empty-state p {
  color: #a1a1aa;
}

.feedback {
  margin-top: 18px;
  padding: 12px 14px;
  border-radius: 8px;
  font-size: 0.95rem;
}

.feedback-success {
  background: rgba(34, 197, 94, 0.1);
  border: 1px solid rgba(34, 197, 94, 0.18);
  color: #4ade80;
}

.feedback-error {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.18);
  color: #f87171;
}

.dashboard-page.theme-light {
  background: #f7f8fb;
  color: #18181b;
}

.dashboard-page.theme-light .dashboard-sidebar {
  background: rgba(255, 255, 255, 0.88);
  color: #18181b;
  border-right-color: #e4e4e7;
}

.dashboard-page.theme-light .sidebar-brand {
  border-bottom-color: #e4e4e7;
}

.dashboard-page.theme-light .sidebar-brand strong,
.dashboard-page.theme-light .sidebar-user strong,
.dashboard-page.theme-light h1,
.dashboard-page.theme-light .panel-head h2,
.dashboard-page.theme-light .bot-status-item strong,
.dashboard-page.theme-light .list-item-main strong,
.dashboard-page.theme-light .monitor-table td,
.dashboard-page.theme-light .message-content,
.dashboard-page.theme-light .message-meta dd {
  color: #18181b;
}

.dashboard-page.theme-light .sidebar-brand small,
.dashboard-page.theme-light .sidebar-user span,
.dashboard-page.theme-light .summary,
.dashboard-page.theme-light .user-line,
.dashboard-page.theme-light .monitor-table th,
.dashboard-page.theme-light .detail-subtitle,
.dashboard-page.theme-light .message-score,
.dashboard-page.theme-light .message-time,
.dashboard-page.theme-light .message-meta dt,
.dashboard-page.theme-light .empty-text {
  color: #71717a;
}

.dashboard-page.theme-light .list-item-main span,
.dashboard-page.theme-light .bot-status-item span {
  color: #a1a1aa;
}

.dashboard-page.theme-light .dashboard-main {
  background: #f7f8fb;
}

.dashboard-page.theme-light .header-row {
  border-bottom-color: #e4e4e7;
}

.dashboard-page.theme-light .sidebar-user,
.dashboard-page.theme-light .panel,
.dashboard-page.theme-light .bot-status-item,
.dashboard-page.theme-light .unsafe-message-item,
.dashboard-page.theme-light .list-item {
  background: #ffffff;
  border-color: #e4e4e7;
  box-shadow: 0 18px 50px rgba(24, 24, 27, 0.06);
}

.dashboard-page.theme-light .nav-btn {
  color: #52525b;
}

.dashboard-page.theme-light .nav-btn:hover {
  background: #f4f4f5;
  color: #18181b;
}

.dashboard-page.theme-light .nav-btn.active {
  background: #f0efff;
  color: #18181b;
}

.dashboard-page.theme-light .pill,
.dashboard-page.theme-light .switch-btn,
.dashboard-page.theme-light .secondary-btn,
.dashboard-page.theme-light .settings-btn,
.dashboard-page.theme-light .setting-row {
  background: #ffffff;
  border-color: #e4e4e7;
  color: #3f3f46;
}

.dashboard-page.theme-light .settings-panel {
  background: rgba(255, 255, 255, 0.72);
  border-color: #e4e4e7;
  box-shadow: 0 18px 40px rgba(24, 24, 27, 0.08);
}

.dashboard-page.theme-light .settings-btn span,
.dashboard-page.theme-light .setting-row span {
  color: #71717a;
}

.dashboard-page.theme-light .settings-btn:hover,
.dashboard-page.theme-light .setting-row:hover,
.dashboard-page.theme-light .secondary-btn:hover:not(:disabled),
.dashboard-page.theme-light .switch-btn:hover:not(:disabled) {
  background: #f4f4f5;
  border-color: #d4d4d8;
}

.dashboard-page.theme-light .monitor-table th,
.dashboard-page.theme-light .monitor-table td {
  border-bottom-color: #e4e4e7;
}

.dashboard-page.theme-light .clickable-row:hover,
.dashboard-page.theme-light .clickable-row:focus {
  background: #f4f4f5;
}

.dashboard-page.theme-light input,
.dashboard-page.theme-light textarea {
  background: #f9fafb;
  border-color: #e4e4e7;
  color: #18181b;
}

.dashboard-page.theme-light input:focus,
.dashboard-page.theme-light textarea:focus {
  outline: none;
  border-color: rgba(108, 92, 231, 0.42);
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(108, 92, 231, 0.1);
}

.dashboard-page.theme-light .message-content {
  background: #f4f4f5;
}

.dashboard-page.theme-light .danger-btn {
  background: #fef2f2;
  border-color: #fecaca;
  color: #b91c1c;
}

.dashboard-page.theme-light .danger-btn:hover:not(:disabled) {
  background: #fee2e2;
  border-color: #fca5a5;
  box-shadow: 0 0 18px rgba(239, 68, 68, 0.12);
}

.dashboard-page.theme-light .bot-online,
.dashboard-page.theme-light .risk-normal,
.dashboard-page.theme-light .feedback-success {
  background: #f0fdf4;
  border-color: #bbf7d0;
  color: #166534;
}

.dashboard-page.theme-light .bot-offline,
.dashboard-page.theme-light .risk-high,
.dashboard-page.theme-light .feedback-error {
  background: #fef2f2;
  border-color: #fecaca;
  color: #b91c1c;
}

.dashboard-page.theme-light .risk-low {
  background: #eff6ff;
  color: #1d4ed8;
}

.dashboard-page.theme-light .risk-medium {
  background: #fffbeb;
  color: #92400e;
}

@media (max-width: 900px) {
  .dashboard-page {
    grid-template-columns: 1fr;
  }

  .dashboard-sidebar {
    min-height: auto;
    position: static;
  }

  .dashboard-main {
    height: auto;
    min-height: 100vh;
    padding: 20px;
  }

  .sidebar-nav {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

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

  .request-form-grid {
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

@media (max-width: 560px) {
  .sidebar-nav {
    grid-template-columns: 1fr;
  }
}
</style>
