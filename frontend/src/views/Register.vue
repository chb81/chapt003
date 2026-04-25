<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2>注册账号</h2>
        </div>
      </template>

      <el-steps :active="step" finish-status="success" simple style="margin-bottom: 20px">
        <el-step title="填写信息" />
        <el-step title="邮箱验证" />
        <el-step title="注册完成" />
      </el-steps>

      <el-form v-if="step === 0" :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="6-20位密码" show-password clearable />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleRegister">注册</el-button>
        </el-form-item>
        <div class="to-login">
          已有账号？<el-link type="primary" @click="$router.push('/login')">去登录</el-link>
        </div>
      </el-form>

      <div v-else-if="step === 1">
        <el-result icon="info" title="验证码已发送" :sub-title="'验证码已发送到 ' + form.email">
          <template #extra>
            <el-alert v-if="devCode" :title="'开发模式验证码: ' + devCode" type="warning" :closable="false" style="margin-bottom: 15px" />
            <el-form :model="verifyForm" label-width="80px">
              <el-form-item label="验证码">
                <el-input v-model="verifyForm.code" placeholder="请输入6位验证码" maxlength="6" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleVerify" :loading="verifying">验证</el-button>
                <el-button @click="handleResend" :loading="resending" :disabled="countdown > 0">
                  {{ countdown > 0 ? countdown + 's后重发' : '重新发送' }}
                </el-button>
              </el-form-item>
            </el-form>
          </template>
        </el-result>
      </div>

      <div v-else>
        <el-result icon="success" title="注册成功" sub-title="您的账号已注册成功，现在可以登录了">
          <template #extra>
            <el-button type="primary" @click="$router.push('/login')">去登录</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { register, verifyEmail, resendVerification } from '@/api/auth'

const router = useRouter()
const loading = ref(false)
const verifying = ref(false)
const resending = ref(false)
const step = ref(0)
const countdown = ref(0)
const devCode = ref('')
const formRef = ref<FormInstance>()

const form = reactive({
  email: '',
  mobile: '',
  password: '',
  confirmPassword: ''
})

const verifyForm = reactive({ code: '' })

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await register(form)
      ElMessage.success(res.message || '注册成功，请查收验证邮件')
      step.value = 1
      startCountdown()
    } catch (e: any) {
      ElMessage.error(e.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}

const handleVerify = async () => {
  if (!verifyForm.code.trim()) { ElMessage.warning('请输入验证码'); return }
  verifying.value = true
  try {
    await verifyEmail(form.email, verifyForm.code)
    ElMessage.success('验证成功')
    step.value = 2
  } catch (e: any) {
    ElMessage.error(e.message || '验证失败')
  } finally {
    verifying.value = false
  }
}

const handleResend = async () => {
  resending.value = true
  try {
    await resendVerification(form.email)
    ElMessage.success('验证码已重新发送')
    startCountdown()
  } catch (e: any) {
    ElMessage.error(e.message || '发送失败')
  } finally {
    resending.value = false
  }
}

const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 480px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
}

.register-header h2 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.to-login {
  text-align: center;
  color: #999;
  font-size: 14px;
}
</style>