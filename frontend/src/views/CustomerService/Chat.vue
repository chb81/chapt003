<template>
  <div class="customer-service">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的会话</span>
              <el-button type="primary" size="small" @click="showNewSession = true">新建会话</el-button>
            </div>
          </template>
          <div v-if="sessions.length === 0" class="empty-text">暂无会话</div>
          <div
            v-for="s in sessions"
            :key="s.id"
            :class="['session-item', { active: currentSessionId === s.id }]"
            @click="selectSession(s)"
          >
            <div class="session-info">
              <span class="session-category">{{ s.category || '未分类' }}</span>
              <el-tag size="small" :type="s.sessionStatus === 'ACTIVE' ? 'success' : 'info'">
                {{ s.sessionStatus === 'ACTIVE' ? '进行中' : s.sessionStatus === 'RESOLVED' ? '已解决' : '已关闭' }}
              </el-tag>
            </div>
            <div class="session-time">{{ formatTime(s.startTime) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card v-if="!currentSessionId" class="chat-empty">
          <el-empty description="请选择或创建一个会话" />
        </el-card>
        <el-card v-else class="chat-area">
          <template #header>
            <span>客服对话 - {{ currentSession?.category || '咨询' }}</span>
          </template>
          <div class="messages" ref="messagesRef">
            <div v-for="msg in messages" :key="msg.id" :class="['message', msg.messageType === 'USER_MESSAGE' ? 'self' : 'other']">
              <div class="message-bubble">
                <div class="message-sender">{{ msg.senderName }}</div>
                <div class="message-content">{{ msg.content }}</div>
                <div class="message-time">{{ formatTime(msg.messageTime) }}</div>
              </div>
            </div>
          </div>
          <div class="input-area">
            <el-input v-model="inputMsg" placeholder="输入消息..." @keyup.enter="sendMsg">
              <template #append>
                <el-button type="primary" @click="sendMsg" :loading="sending">发送</el-button>
              </template>
            </el-input>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showNewSession" title="新建客服会话" width="400px">
      <el-form :model="sessionForm" label-width="80px">
        <el-form-item label="问题分类">
          <el-select v-model="sessionForm.category" placeholder="选择分类">
            <el-option label="志愿填报" value="志愿填报" />
            <el-option label="技术咨询" value="技术咨询" />
            <el-option label="账号问题" value="账号问题" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="sessionForm.initialMessage" type="textarea" :rows="3" placeholder="请描述您的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewSession = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSession">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserSessions, createSession, getSessionMessages, sendMessage } from '@/api/customerService'

const sessions = ref<any[]>([])
const currentSessionId = ref<number | null>(null)
const currentSession = ref<any>(null)
const messages = ref<any[]>([])
const inputMsg = ref('')
const sending = ref(false)
const showNewSession = ref(false)
const messagesRef = ref<HTMLElement>()

const sessionForm = reactive({ category: '', initialMessage: '' })

const formatTime = (t: string) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadSessions = async () => {
  try {
    const res = await getUserSessions()
    sessions.value = res.data || []
  } catch (e) { sessions.value = [] }
}

const selectSession = async (s: any) => {
  currentSessionId.value = s.id
  currentSession.value = s
  try {
    const res = await getSessionMessages(s.id)
    messages.value = res.data || []
    await nextTick()
    scrollToBottom()
  } catch (e) { messages.value = [] }
}

const sendMsg = async () => {
  if (!inputMsg.value.trim() || !currentSessionId.value) return
  sending.value = true
  try {
    await sendMessage(currentSessionId.value, { content: inputMsg.value })
    inputMsg.value = ''
    const res = await getSessionMessages(currentSessionId.value)
    messages.value = res.data || []
    await nextTick()
    scrollToBottom()
  } catch (e: any) {
    ElMessage.error(e.message || '发送失败')
  } finally {
    sending.value = false
  }
}

const handleCreateSession = async () => {
  if (!sessionForm.initialMessage.trim()) { ElMessage.warning('请描述问题'); return }
  try {
    await createSession(sessionForm)
    ElMessage.success('会话已创建')
    showNewSession.value = false
    Object.assign(sessionForm, { category: '', initialMessage: '' })
    loadSessions()
  } catch (e: any) {
    ElMessage.error(e.message || '创建失败')
  }
}

const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

onMounted(() => { loadSessions() })
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 20px;
}

.session-item {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.session-item:hover, .session-item.active {
  background: #ecf5ff;
}

.session-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.session-category {
  font-size: 14px;
  font-weight: 500;
}

.session-time {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}

.chat-empty {
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-area {
  display: flex;
  flex-direction: column;
}

.messages {
  height: 400px;
  overflow-y: auto;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.message {
  margin-bottom: 15px;
  display: flex;
}

.message.self {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.message.self .message-bubble {
  background: #409eff;
  color: #fff;
}

.message-sender {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.message-content {
  font-size: 14px;
  line-height: 1.5;
}

.message-time {
  font-size: 11px;
  opacity: 0.6;
  margin-top: 4px;
  text-align: right;
}

.input-area {
  margin-top: 15px;
}
</style>