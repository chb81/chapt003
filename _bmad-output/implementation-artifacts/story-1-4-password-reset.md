# Story 1.4: 密码重置

## Story Metadata

- **Story ID**: 1-4-password-reset
- **Epic**: Epic 1 - 用户认证与个人资料
- **Title**: 密码重置
- **Status**: ready-for-dev
- **Priority**: High
- **Story Points**: 5
- **Estimated Days**: 2-3

## User Story

作为一个忘记密码的用户，
我希望能够通过邮箱或短信重置密码，
以便重新访问我的账户。

## Acceptance Criteria

### AC1: 访问密码重置功能

**Given** 用户在登录页面
**When** 用户点击"忘记密码"链接
**Then** 系统应跳转到密码重置请求页面

### AC2: 发送验证码

**Given** 用户在密码重置请求页面
**When** 用户输入注册的邮箱或手机号
**And** 用户点击"发送验证码"按钮
**Then** 系统应验证邮箱/手机号是否存在
**And** 系统应发送验证码到用户的邮箱或手机
**And** 系统应显示"验证码已发送"的成功提示

### AC3: 重置密码成功

**Given** 用户在密码重置页面
**When** 用户输入正确的验证码
**And** 用户输入新密码（符合密码强度要求）
**And** 用户确认新密码
**And** 用户点击"重置密码"按钮
**Then** 系统应验证验证码是否有效且未过期
**And** 系统应验证两次密码输入是否一致
**And** 系统应更新用户密码
**And** 系统应显示"密码重置成功"的成功提示
**And** 系统应重定向到登录页面

### AC4: 验证码错误或过期

**Given** 用户在密码重置页面
**When** 用户输入错误或过期的验证码
**Then** 系统应显示"验证码错误或已过期"的错误提示

### AC5: 密码不一致

**Given** 用户在密码重置页面
**When** 用户输入不一致的新密码
**Then** 系统应显示"两次密码输入不一致"的错误提示

## Technical Requirements

### Backend API Endpoints

#### 1. 发送密码重置验证码

**Endpoint:** `POST /api/v1/auth/password-reset/send-code`

**Request Body:**
```json
{
  "email": "user@example.com",
  "mobile": "13800138000"
}
```

**Note:** Only one of `email` or `mobile` should be provided.

**Response (Success - 200):**
```json
{
  "code": 0,
  "message": "验证码已发送",
  "data": null,
  "timestamp": 1715520000000
}
```

**Response (User Not Found - 404):**
```json
{
  "code": 404,
  "message": "该邮箱/手机号未注册",
  "data": null,
  "timestamp": 1715520000000
}
```

**Response (Rate Limited - 429):**
```json
{
  "code": 429,
  "message": "发送频率过高，请稍后重试",
  "data": null,
  "timestamp": 1715520000000
}
```

#### 2. 重置密码

**Endpoint:** `POST /api/v1/auth/password-reset/verify`

**Request Body:**
```json
{
  "email": "user@example.com",
  "mobile": "13800138000",
  "verification_code": "123456",
  "new_password": "NewPassword123",
  "confirm_password": "NewPassword123"
}
```

**Response (Success - 200):**
```json
{
  "code": 0,
  "message": "密码重置成功",
  "data": null,
  "timestamp": 1715520000000
}
```

**Response (Invalid Code - 400):**
```json
{
  "code": 400,
  "message": "验证码错误或已过期",
  "data": null,
  "timestamp": 1715520000000
}
```

**Response (Password Mismatch - 400):**
```json
{
  "code": 400,
  "message": "两次密码输入不一致",
  "data": null,
  "timestamp": 1715520000000
}
```

**Response (Weak Password - 400):**
```json
{
  "code": 400,
  "message": "密码至少8位，包含字母和数字",
  "data": null,
  "timestamp": 1715520000000
}
```

### Backend Implementation Details

#### Service Layer

**Create `PasswordResetService.java`:**

```java
@Service
public class PasswordResetService {
    
    private static final int VERIFICATION_CODE_EXPIRY_MINUTES = 10;
    private static final int MAX_SEND_ATTEMPTS_PER_HOUR = 5;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationCodeService verificationCodeService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    public void sendVerificationCode(String email, String mobile) {
        User user = null;
        String target = null;
        VerificationCodeType type = null;
        
        if (email != null && !email.isEmpty()) {
            user = userRepository.findByEmail(email);
            target = email;
            type = VerificationCodeType.EMAIL_PASSWORD_RESET;
        } else if (mobile != null && !mobile.isEmpty()) {
            user = userRepository.findByMobile(mobile);
            target = mobile;
            type = VerificationCodeType.SMS_PASSWORD_RESET;
        } else {
            throw new BusinessException("请提供邮箱或手机号");
        }
        
        if (user == null) {
            throw new BusinessException("该邮箱/手机号未注册");
        }
        
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException("账户已被禁用，请联系管理员");
        }
        
        verificationCodeService.checkSendRateLimit(target, type, MAX_SEND_ATTEMPTS_PER_HOUR);
        
        String code = verificationCodeService.generateAndStoreCode(target, type, VERIFICATION_CODE_EXPIRY_MINUTES);
        
        if (type == VerificationCodeType.EMAIL_PASSWORD_RESET) {
            emailService.sendPasswordResetCode(email, code);
        } else {
            smsService.sendPasswordResetCode(mobile, code);
        }
    }
    
    @Transactional
    public void resetPassword(String email, String mobile, String code, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("两次密码输入不一致");
        }
        
        if (!isPasswordStrong(newPassword)) {
            throw new BusinessException("密码至少8位，包含字母和数字");
        }
        
        User user = null;
        String target = null;
        VerificationCodeType type = null;
        
        if (email != null && !email.isEmpty()) {
            user = userRepository.findByEmail(email);
            target = email;
            type = VerificationCodeType.EMAIL_PASSWORD_RESET;
        } else if (mobile != null && !mobile.isEmpty()) {
            user = userRepository.findByMobile(mobile);
            target = mobile;
            type = VerificationCodeType.SMS_PASSWORD_RESET;
        } else {
            throw new BusinessException("请提供邮箱或手机号");
        }
        
        if (user == null) {
            throw new BusinessException("该邮箱/手机号未注册");
        }
        
        if (!verificationCodeService.verifyCode(target, type, code)) {
            throw new BusinessException("验证码错误或已过期");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        verificationCodeService.invalidateCode(target, type);
    }
    
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
}
```

#### Controller Layer

**Create `PasswordResetController.java`:**

```java
@RestController
@RequestMapping("/api/v1/auth/password-reset")
public class PasswordResetController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(
            @RequestBody PasswordResetSendCodeRequest request) {
        passwordResetService.sendVerificationCode(request.getEmail(), request.getMobile());
        return ResponseEntity.ok(ApiResponse.success("验证码已发送", null));
    }
    
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody PasswordResetVerifyRequest request) {
        passwordResetService.resetPassword(
            request.getEmail(),
            request.getMobile(),
            request.getVerificationCode(),
            request.getNewPassword(),
            request.getConfirmPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("密码重置成功", null));
    }
}
```

#### DTOs

**Create `PasswordResetSendCodeRequest.java`:**

```java
@Data
public class PasswordResetSendCodeRequest {
    @NotBlank(message = "邮箱或手机号不能为空")
    @Email(message = "邮箱格式不正确", regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
}
```

**Create `PasswordResetVerifyRequest.java`:**

```java
@Data
public class PasswordResetVerifyRequest {
    @NotBlank(message = "邮箱或手机号不能为空")
    @Email(message = "邮箱格式不正确", regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码格式不正确")
    private String verificationCode;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "密码至少8位")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    private String newPassword;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
```

### Frontend Implementation Details

#### Miniprogram Pages

**1. Password Reset Request Page (`frontend/pages/password-reset-request/password-reset.wxml`):**

```xml
<view class="container">
  <view class="header">
    <text class="title">重置密码</text>
    <text class="subtitle">输入注册的邮箱或手机号获取验证码</text>
  </view>

  <view class="form">
    <van-field
      value="{{ emailOrMobile }}"
      label="邮箱/手机号"
      placeholder="请输入邮箱或手机号"
      bind:change="onEmailOrMobileChange"
    />

    <van-button 
      type="primary" 
      block 
      loading="{{ sending }}"
      loading-text="发送中..."
      bind:click="sendVerificationCode"
      custom-class="submit-btn"
    >
      发送验证码
    </van-button>
  </view>

  <view class="tips">
    <text class="tip">验证码将发送到您的邮箱或手机，有效期为10分钟</text>
  </view>
</view>
```

**2. Password Reset Verify Page (`frontend/pages/password-reset-verify/password-reset.wxml`):**

```xml
<view class="container">
  <view class="header">
    <text class="title">设置新密码</text>
    <text class="subtitle">请输入验证码和新密码</text>
  </view>

  <view class="form">
    <van-field
      value="{{ verificationCode }}"
      label="验证码"
      placeholder="请输入6位验证码"
      maxlength="6"
      type="number"
      bind:change="onVerificationCodeChange"
    />

    <van-field
      value="{{ newPassword }}"
      label="新密码"
      placeholder="至少8位，包含字母和数字"
      password
      bind:change="onNewPasswordChange"
    />

    <van-field
      value="{{ confirmPassword }}"
      label="确认密码"
      placeholder="再次输入新密码"
      password
      bind:change="onConfirmPasswordChange"
    />

    <van-button 
      type="primary" 
      block 
      loading="{{ resetting }}"
      loading-text="重置中..."
      bind:click="resetPassword"
      custom-class="submit-btn"
    >
      重置密码
    </van-button>
  </view>

  <view class="password-strength" wx:if="{{ newPassword }}">
    <text class="strength-label">密码强度：</text>
    <view class="strength-indicator {{ strengthClass }}">
      <text class="strength-text">{{ strengthText }}</text>
    </view>
  </view>
</view>
```

**3. Password Reset Request Logic (`frontend/pages/password-reset-request/password-reset.js`):**

```javascript
Page({
  data: {
    emailOrMobile: '',
    sending: false
  },

  onEmailOrMobileChange(e) {
    this.setData({ emailOrMobile: e.detail })
  },

  async sendVerificationCode() {
    const { emailOrMobile } = this.data

    if (!emailOrMobile) {
      wx.showToast({
        title: '请输入邮箱或手机号',
        icon: 'none'
      })
      return
    }

    const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailOrMobile)
    const isMobile = /^1[3-9]\d{9}$/.test(emailOrMobile)

    if (!isEmail && !isMobile) {
      wx.showToast({
        title: '邮箱或手机号格式不正确',
        icon: 'none'
      })
      return
    }

    this.setData({ sending: true })

    try {
      const payload = isEmail ? { email: emailOrMobile } : { mobile: emailOrMobile }
      
      const res = await wx.request({
        url: `${app.globalData.apiBaseUrl}/api/v1/auth/password-reset/send-code`,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        data: payload
      })

      if (res.data.code === 0) {
        wx.showToast({
          title: '验证码已发送',
          icon: 'success'
        })

        wx.navigateTo({
          url: `/pages/password-reset-verify/password-reset-verify?${isEmail ? 'email' : 'mobile'}=${emailOrMobile}`
        })
      } else {
        wx.showToast({
          title: res.data.message || '发送失败',
          icon: 'none'
        })
      }
    } catch (error) {
      wx.showToast({
        title: '网络错误，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ sending: false })
    }
  }
})
```

**4. Password Reset Verify Logic (`frontend/pages/password-reset-verify/password-reset.js`):**

```javascript
Page({
  data: {
    email: '',
    mobile: '',
    verificationCode: '',
    newPassword: '',
    confirmPassword: '',
    resetting: false,
    strengthClass: '',
    strengthText: ''
  },

  onLoad(options) {
    if (options.email) {
      this.setData({ email: options.email })
    } else if (options.mobile) {
      this.setData({ mobile: options.mobile })
    }
  },

  onVerificationCodeChange(e) {
    this.setData({ verificationCode: e.detail })
  },

  onNewPasswordChange(e) {
    this.setData({ newPassword: e.detail })
    this.checkPasswordStrength(e.detail)
  },

  onConfirmPasswordChange(e) {
    this.setData({ confirmPassword: e.detail })
  },

  checkPasswordStrength(password) {
    if (!password) {
      this.setData({ strengthClass: '', strengthText: '' })
      return
    }

    let score = 0
    if (password.length >= 8) score++
    if (/[A-Za-z]/.test(password)) score++
    if (/\d/.test(password)) score++
    if (/[!@#$%^&*]/.test(password)) score++

    const strengthMap = {
      0: { class: 'weak', text: '弱' },
      1: { class: 'weak', text: '弱' },
      2: { class: 'medium', text: '中' },
      3: { class: 'strong', text: '强' },
      4: { class: 'strong', text: '很强' }
    }

    this.setData({
      strengthClass: strengthMap[score].class,
      strengthText: strengthMap[score].text
    })
  },

  async resetPassword() {
    const { email, mobile, verificationCode, newPassword, confirmPassword } = this.data

    if (!verificationCode) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'none'
      })
      return
    }

    if (!newPassword) {
      wx.showToast({
        title: '请输入新密码',
        icon: 'none'
      })
      return
    }

    if (newPassword.length < 8) {
      wx.showToast({
        title: '密码至少8位',
        icon: 'none'
      })
      return
    }

    if (!/[A-Za-z]/.test(newPassword) || !/\d/.test(newPassword)) {
      wx.showToast({
        title: '密码必须包含字母和数字',
        icon: 'none'
      })
      return
    }

    if (newPassword !== confirmPassword) {
      wx.showToast({
        title: '两次密码输入不一致',
        icon: 'none'
      })
      return
    }

    this.setData({ resetting: true })

    try {
      const payload = email 
        ? { email, verificationCode, newPassword, confirmPassword }
        : { mobile, verificationCode, newPassword, confirmPassword }
      
      const res = await wx.request({
        url: `${app.globalData.apiBaseUrl}/api/v1/auth/password-reset/verify`,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        data: payload
      })

      if (res.data.code === 0) {
        wx.showToast({
          title: '密码重置成功',
          icon: 'success'
        })

        setTimeout(() => {
          wx.redirectTo({
            url: '/pages/login/login'
          })
        }, 1500)
      } else {
        wx.showToast({
          title: res.data.message || '重置失败',
          icon: 'none'
        })
      }
    } catch (error) {
      wx.showToast({
        title: '网络错误，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ resetting: false })
    }
  }
})
```

**5. Add "Forgot Password" Link to Login Page (`frontend/pages/login/login.wxml`):**

```xml
<view class="form-footer">
  <text class="link" bind:tap="goToPasswordReset">忘记密码？</text>
</view>
```

**6. Add Navigation Handler (`frontend/pages/login/login.js`):**

```javascript
goToPasswordReset() {
  wx.navigateTo({
    url: '/pages/password-reset-request/password-reset-request'
  })
}
```

### Database Changes

No database schema changes required for this story. The verification codes will be stored in Redis (as per architecture) with appropriate TTL.

### Dependencies

**New Dependencies to Add:**

1. **Email Service** (for sending password reset emails)
   - Spring Boot Starter Mail
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-mail</artifactId>
   </dependency>
   ```

2. **SMS Service** (for sending password reset SMS)
   - Alibaba Cloud SMS SDK or Tencent Cloud SMS SDK
   ```xml
   <dependency>
       <groupId>com.aliyun</groupId>
       <artifactId>aliyun-java-sdk-core</artifactId>
       <version>4.6.0</version>
   </dependency>
   <dependency>
       <groupId>com.aliyun</groupId>
       <artifactId>aliyun-java-sdk-dysmsapi</artifactId>
       <version>2.1.0</version>
   </dependency>
   ```

**Existing Dependencies:**
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- Spring Boot Starter Data Redis
- Validation API

### Configuration

**Add to `application.yml`:**

```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    protocol: smtp
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

aliyun:
  sms:
    access-key-id: ${ALIYUN_SMS_ACCESS_KEY_ID}
    access-key-secret: ${ALIYUN_SMS_ACCESS_KEY_SECRET}
    sign-name: ${ALIYUN_SMS_SIGN_NAME}
    template-code: ${ALIYUN_SMS_TEMPLATE_CODE}

verification:
  code:
    expiry-minutes: 10
    max-attempts-per-hour: 5
```

### Security Considerations

1. **Rate Limiting**: Limit verification code sends to prevent abuse
2. **Code Expiry**: Verification codes expire after 10 minutes
3. **Code Invalidation**: Invalidate code after successful password reset
4. **Password Encryption**: Use BCrypt for password hashing
5. **Secure Transport**: Use HTTPS for all API calls
6. **Input Validation**: Validate all inputs on both frontend and backend
7. **Logging**: Log all password reset attempts for audit

### Testing Strategy

#### Unit Tests

1. **PasswordResetService Tests**:
   - Test sending verification code via email
   - Test sending verification code via SMS
   - Test rate limiting
   - Test user not found scenario
   - Test disabled user scenario
   - Test password reset with valid code
   - Test password reset with invalid code
   - Test password reset with mismatched passwords
   - Test password strength validation

2. **PasswordResetController Tests**:
   - Test send-code endpoint
   - Test verify endpoint
   - Test error responses

#### Integration Tests

1. Test complete password reset flow via email
2. Test complete password reset flow via SMS
3. Test error scenarios

#### E2E Tests (Manual)

1. User clicks "forgot password" link
2. User enters email and sends verification code
3. User receives email with verification code
4. User enters verification code and new password
5. User successfully resets password
6. User logs in with new password

### Dependencies on Other Stories

- Depends on **Story 1.1** (User Registration) - User must exist in system
- Depends on **Story 1.2** (User Login) - Login page needs "forgot password" link

### Lessons Learned from Story 1.3

1. **Always validate String operations**: Add bounds checking for string operations
2. **Reset UI states**: Always reset loading/processing states on all error paths
3. **Handle concurrency**: Use database constraints and exception handling for concurrent operations
4. **Security first**: Never use weak passwords; enforce password strength
5. **Comprehensive logging**: Log all authentication attempts for audit and debugging

### Implementation Checklist

- [ ] Create `PasswordResetService.java`
- [ ] Create `PasswordResetController.java`
- [ ] Create DTOs (`PasswordResetSendCodeRequest.java`, `PasswordResetVerifyRequest.java`)
- [ ] Create `EmailService.java` for sending emails
- [ ] Create `SmsService.java` for sending SMS
- [ ] Add email template for password reset
- [ ] Add SMS template for password reset
- [ ] Create `password-reset-request` page (WXML, WXSS, JS, JSON)
- [ ] Create `password-reset-verify` page (WXML, WXSS, JS, JSON)
- [ ] Add "forgot password" link to login page
- [ ] Add navigation handler in login page
- [ ] Update `application.yml` with email and SMS configuration
- [ ] Add Spring Boot Starter Mail dependency
- [ ] Add SMS SDK dependency
- [ ] Implement rate limiting for verification code sends
- [ ] Add logging for password reset attempts
- [ ] Write unit tests for service layer
- [ ] Write unit tests for controller layer
- [ ] Write integration tests
- [ ] Perform manual E2E testing
- [ ] Update API documentation

### Acceptance Criteria Verification

- [ ] AC1: User can access password reset from login page
- [ ] AC2: System sends verification code to email/phone
- [ ] AC3: User can reset password with valid code
- [ ] AC4: System shows error for invalid/expired code
- [ ] AC5: System shows error for mismatched passwords

### Definition of Done

- [ ] All acceptance criteria met
- [ ] Code reviewed and approved
- [ ] Unit tests written and passing
- [ ] Integration tests written and passing
- [ ] Manual E2E testing completed
- [ ] API documentation updated
- [ ] Security review completed
- [ ] Performance tested (response time < 3 seconds)
- [ ] Code follows project coding standards
- [ ] No critical or high severity bugs
- [ ] Story marked as "done" in sprint tracking

---

**Created:** 2026-04-12
**Last Updated:** 2026-04-12
**Story Owner:** To be assigned
**Reviewer:** To be assigned
