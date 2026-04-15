<template>
  <div class="user-detail-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
          <h2>用户详情</h2>
          <div></div>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ userDetail?.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userDetail?.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userDetail?.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userDetail?.mobile || '-' }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag :type="userDetail?.role === 'ADMIN' ? 'danger' : 'primary'">
                {{ userDetail?.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(userDetail?.status)">
                {{ getStatusText(userDetail?.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱验证">
              <el-tag :type="userDetail?.emailVerified ? 'success' : 'warning'">
                {{ userDetail?.emailVerified ? '已验证' : '未验证' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="手机验证">
              <el-tag :type="userDetail?.mobileVerified ? 'success' : 'warning'">
                {{ userDetail?.mobileVerified ? '已验证' : '未验证' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ formatDate(userDetail?.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="最后登录">
              {{ userDetail?.lastLoginAt ? formatDate(userDetail.lastLoginAt) : '-' }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="action-buttons">
            <el-button type="primary" @click="showRoleDialog">修改角色</el-button>
            <el-button
              :type="userDetail?.status === 'ACTIVE' ? 'warning' : 'success'"
              @click="toggleUserStatus"
            >
              {{ userDetail?.status === 'ACTIVE' ? '禁用用户' : '启用用户' }}
            </el-button>
            <el-button type="danger" @click="handleDeleteUser">删除用户</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane label="登录历史" name="loginHistory">
          <el-timeline v-if="userDetail?.loginHistory && userDetail.loginHistory.length > 0">
            <el-timeline-item
              v-for="item in userDetail.loginHistory"
              :key="item.id"
              :timestamp="formatDate(item.loginTime)"
            >
              <div class="timeline-item">
                <div><strong>IP地址:</strong> {{ item.ipAddress }}</div>
                <div><strong>登录方式:</strong> {{ item.loginMethod === 'WECHAT' ? '微信' : '密码' }}</div>
                <div><strong>设备:</strong> {{ item.userAgent }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无登录历史" />
        </el-tab-pane>

        <el-tab-pane label="操作日志" name="auditLogs">
          <el-timeline v-if="userDetail?.auditLogs && userDetail.auditLogs.length > 0">
            <el-timeline-item
              v-for="item in userDetail.auditLogs"
              :key="item.id"
              :timestamp="formatDate(item.timestamp)"
            >
              <div class="timeline-item">
                <div><strong>操作:</strong> {{ item.action }}</div>
                <div v-if="item.entityType"><strong>实体类型:</strong> {{ item.entityType }}</div>
                <div v-if="item.oldValue && item.newValue">
                  <strong>变更:</strong> {{ item.oldValue }} → {{ item.newValue }}
                </div>
                <div><strong>操作人:</strong> {{ item.adminEmail }} (ID: {{ item.adminId }})</div>
                <div v-if="item.ipAddress"><strong>IP地址:</strong> {{ item.ipAddress }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无操作日志" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="roleDialogVisible"
      title="修改用户角色"
      width="400px"
    >
      <el-form>
        <el-form-item label="用户角色">
          <el-select v-model="selectedRole" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="普通用户" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateRole">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserDetail, updateUserRole, updateUserStatus, deleteUser, getLoginHistory, getAuditLogs } from '@/api/user'
import type { UserDetail, UserRole, UserStatus } from '@/types'

const route = useRoute()
const router = useRouter()
const activeTab = ref('basic')
const userDetail = ref<UserDetail | null>(null)
const roleDialogVisible = ref(false)
const selectedRole = ref<UserRole>('USER')

const loadUserDetail = async () => {
  const userId = Number(route.params.id)
  try {
    const [detailRes, loginHistoryRes, auditLogsRes] = await Promise.all([
      getUserDetail(userId),
      getLoginHistory(userId, 10),
      getAuditLogs(userId, 20)
    ])
    userDetail.value = {
      ...detailRes,
      loginHistory: loginHistoryRes,
      auditLogs: auditLogsRes
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载用户详情失败')
  }
}

const goBack = () => {
  router.back()
}

const showRoleDialog = () => {
  selectedRole.value = userDetail.value?.role || 'USER'
  roleDialogVisible.value = true
}

const handleUpdateRole = async () => {
  const userId = Number(route.params.id)
  try {
    await updateUserRole(userId, { role: selectedRole.value })
    ElMessage.success('角色修改成功')
    roleDialogVisible.value = false
    loadUserDetail()
  } catch (error: any) {
    ElMessage.error(error.message || '角色修改失败')
  }
}

const toggleUserStatus = async () => {
  const userId = Number(route.params.id)
  const currentStatus = userDetail.value?.status
  const newStatus: UserStatus = currentStatus === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
  const actionText = newStatus === 'DISABLED' ? '禁用' : '启用'

  try {
    await ElMessageBox.confirm(`确定要${actionText}该用户吗？`, '确认操作', {
      type: 'warning'
    })
    await updateUserStatus(userId, { status: newStatus })
    ElMessage.success(`用户已${actionText}`)
    loadUserDetail()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleDeleteUser = async () => {
  const userId = Number(route.params.id)
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？删除后无法恢复！', '确认删除', {
      type: 'error',
      confirmButtonText: '确定删除',
      confirmButtonClass: 'el-button--danger'
    })
    await deleteUser(userId)
    ElMessage.success('用户已删除')
    router.push('/users')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const getStatusType = (status?: string) => {
  if (!status) return ''
  const typeMap: Record<string, any> = {
    ACTIVE: 'success',
    DISABLED: 'danger',
    UNVERIFIED: 'warning',
    DELETED: 'info'
  }
  return typeMap[status] || ''
}

const getStatusText = (status?: string) => {
  if (!status) return ''
  const textMap: Record<string, string> = {
    ACTIVE: '正常',
    DISABLED: '禁用',
    UNVERIFIED: '未验证',
    DELETED: '已删除'
  }
  return textMap[status] || status
}

const formatDate = (date?: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadUserDetail()
})
</script>

<style scoped>
.user-detail-container {
  padding: 20px;
}

.card-header {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 20px;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.action-buttons {
  margin-top: 30px;
  display: flex;
  gap: 10px;
}

.timeline-item {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.timeline-item div {
  margin-bottom: 5px;
}

.timeline-item div:last-child {
  margin-bottom: 0;
}
</style>
