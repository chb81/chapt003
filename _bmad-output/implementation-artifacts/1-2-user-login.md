# Story 1.2: User Login

## Story Header

**Story ID:** 1.2  
**Epic:** Epic 1 - User Account Management  
**Title:** 用户登录 (User Login)  
**Status:** ready-for-dev  
**Priority:** High  
**Story Points:** 5  
**Assigned To:** TBD  
**Created:** 2026-04-12  
**Last Updated:** 2026-04-12  

## User Story

作为一个已注册用户，
我希望能够使用邮箱/手机号和密码登录系统，
以便访问我的个人数据和系统功能。

## Acceptance Criteria

### AC-1: Valid Credentials
**Given** 用户已经注册并验证了邮箱/手机号  
**When** 用户输入正确的邮箱/手机号和密码  
**Then** 系统验证凭证成功  
**And** 生成 JWT token  
**And** 将用户重定向到首页  
**And** 显示"登录成功"消息

### AC-2: Wrong Password
**Given** 用户已注册  
**When** 用户输入正确的邮箱/手机号但密码错误  
**Then** 系统返回"邮箱/手机号或密码错误"消息  
**And** 不生成任何 token

### AC-3: Non-existent Email/Mobile
**When** 用户输入不存在的邮箱/手机号  
**Then** 系统返回"邮箱/手机号或密码错误"消息  
**And** 不生成任何 token  
**And** 不透露用户是否存在（安全考虑）

### AC-4: Unverified Account
**Given** 用户已注册但未验证邮箱/手机号  
**When** 用户尝试登录  
**Then** 系统返回"账户未验证，请先验证邮箱/手机号"消息  
**And** 不生成任何 token

### AC-5: JWT Token Expired
**Given** 用户已登录并持有有效的 JWT token  
**When** token 过期  
**Then** 系统将用户重定向到登录页面  
**And** 显示"登录已过期，请重新登录"消息

## Task List

### Backend Tasks
- [ ] Create LoginRequest DTO with email/mobile and password fields
- [ ] Create LoginResponse DTO with token and user info
- [ ] Implement JWT utility class for token generation and validation
- [ ] Add login method to AuthService with credential validation
- [ ] Add user status check (VERIFIED only) in login logic
- [ ] Implement JWT token generation with 24-hour expiration
- [ ] Add login endpoint to AuthController
- [ ] Add rate limiting to login endpoint (10 attempts/minute/IP)
- [ ] Add audit logging for login attempts (success/failure)
- [ ] Create JwtAuthenticationFilter for token validation
- [ ] Configure Spring Security with JWT authentication
- [ ] Add password encoding verification using BCrypt
- [ ] Create custom exception for unverified account
- [ ] Update GlobalExceptionHandler for login-specific errors

### Frontend Tasks
- [ ] Create login page (pages/login/login.wxml, login.wxss, login.js, login.json)
- [ ] Implement login form with email/mobile and password fields
- [ ] Add show/hide password toggle
- [ ] Add "Remember me" checkbox
- [ ] Implement login API call with request.js
- [ ] Store JWT token in wx.setStorageSync on successful login
- [ ] Store user info in app.globalData
- [ ] Redirect to index page on successful login
- [ ] Display error messages using wx.showToast
- [ ] Add loading state during login request
- [ ] Implement auto-login check on app launch
- [ ] Add token expiration handling in request interceptor
- [ ] Add "Forgot Password" link (placeholder for future story)
- [ ] Add "Register" link to navigate to registration page

### Testing Tasks
- [ ] Create unit tests for AuthService.login()
- [ ] Create unit tests for JwtUtil class
- [ ] Create unit tests for JwtAuthenticationFilter
- [ ] Create integration tests for login endpoint
- [ ] Test valid credentials flow
- [ ] Test wrong password flow
- [ ] Test non-existent user flow
- [ ] Test unverified account flow
- [ ] Test token generation and validation
- [ ] Test token expiration handling
- [ ] Test rate limiting on login endpoint
- [ ] Test audit logging
- [ ] Test concurrent login attempts
- [ ] Test SQL injection prevention
- [ ] Test XSS prevention in error messages

### Documentation Tasks
- [ ] Update API documentation with login endpoint
- [ ] Document JWT token structure and claims
- [ ] Document authentication flow
- [ ] Document error codes and messages
- [ ] Update project documentation with security considerations

## Development Notes

### Architecture Requirements

**Authentication Flow:**
1. User submits email/mobile + password
2. Backend validates credentials using BCrypt
3. Backend checks user status (must be VERIFIED)
4. Backend generates JWT token with claims (userId, email, role, expiration)
5. Backend returns token and user info to frontend
6. Frontend stores token in wx.setStorageSync
7. Frontend includes token in subsequent API requests (Authorization header)
8. Backend validates token on protected endpoints

**JWT Token Structure:**
```json
{
  "sub": "userId",
  "email": "user@example.com",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

**Security Requirements:**
- Use BCrypt for password verification (strength 12, same as registration)
- Generate JWT tokens with HS256 algorithm
- Set token expiration to 24 hours
- Include userId, email, role in token claims
- Sign JWT with secret key from application.yml
- Validate token signature and expiration on each request
- Implement rate limiting: 10 login attempts/minute/IP
- Never log passwords or JWT tokens
- Use constant-time comparison for password verification
- Sanitize all error messages to prevent information disclosure

**Learnings from Story 1.1 Review:**
- ✅ Use constructor injection instead of field injection
- ✅ Add rate limiting to prevent brute force attacks
- ✅ Use SecureRandom for any random number generation
- ✅ Store sensitive configuration in environment variables
- ✅ Implement proper error sanitization in GlobalExceptionHandler
- ✅ Add comprehensive audit logging
- ✅ Add request logging for security monitoring
- ✅ Use @NotBlank validation on all request fields
- ✅ Wrap database operations in transactions
- ✅ Add comprehensive input validation with @Pattern and @Size
- ✅ Cache expensive operations (e.g., repeated isExpired() checks)
- ✅ Ensure consistent PasswordEncoder usage
- ✅ Check user status before allowing operations

### Backend Implementation Details

**LoginRequest.java:**
```java
@Data
public class LoginRequest {
    @NotBlank(message = "邮箱或手机号不能为空")
    private String emailOrMobile;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 50, message = "密码长度必须在8-50个字符之间")
    private String password;
}
```

**LoginResponse.java:**
```java
@Data
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String mobile;
    private String role;
    private Long expiresIn; // seconds until expiration
}
```

**JwtUtil.java:**
```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        return createToken(claims, user.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
```

**AuthService.login() method:**
```java
@Transactional
public LoginResponse login(LoginRequest request) {
    // Find user by email or mobile
    User user = userRepository.findByEmailOrMobile(request.getEmailOrMobile())
        .orElseThrow(() -> new AuthenticationException("邮箱/手机号或密码错误"));

    // Check if user is verified
    if (user.getStatus() != UserStatus.VERIFIED) {
        throw new UnverifiedAccountException("账户未验证，请先验证邮箱/手机号");
    }

    // Verify password
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        throw new AuthenticationException("邮箱/手机号或密码错误");
    }

    // Generate JWT token
    String token = jwtUtil.generateToken(user);

    // Log successful login
    log.info("User login successful: userId={}, email={}", user.getId(), user.getEmail());

    return LoginResponse.builder()
        .token(token)
        .userId(user.getId())
        .email(user.getEmail())
        .mobile(user.getMobile())
        .role(user.getRole().name())
        .expiresIn(jwtExpiration / 1000)
        .build();
}
```

**AuthController.login() endpoint:**
```java
@PostMapping("/login")
@RateLimit(value = 10, interval = 60) // 10 attempts per minute
public ResponseEntity<ApiResponse<LoginResponse>> login(
    @Valid @RequestBody LoginRequest request,
    HttpServletRequest httpRequest) {
    
    log.info("Login attempt from IP: {}", getClientIp(httpRequest));
    
    LoginResponse response = authService.login(request);
    
    return ResponseEntity.ok(
        ApiResponse.success("登录成功", response)
    );
}
```

**JwtAuthenticationFilter.java:**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(username).orElse(null);
            
            if (user != null && jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**Spring Security Configuration:**
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jjwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

**Custom Exceptions:**
```java
public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(40001, message);
    }
}

public class UnverifiedAccountException extends BusinessException {
    public UnverifiedAccountException(String message) {
        super(40002, message);
    }
}

public class TokenExpiredException extends BusinessException {
    public TokenExpiredException(String message) {
        super(40003, message);
    }
}
```

**Rate Limiting Annotation:**
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int value() default 10;
    int interval() default 60; // seconds
}
```

**Rate Limiting Aspect:**
```java
@Aspect
@Component
public class RateLimitAspect {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = getClientIp(request);
        
        RateLimiter limiter = limiters.computeIfAbsent(
            ip, 
            k -> RateLimiter.create(rateLimit.value() / (double) rateLimit.interval())
        );
        
        if (!limiter.tryAcquire()) {
            throw new BusinessException(40004, "请求过于频繁，请稍后再试");
        }
        
        return joinPoint.proceed();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
```

### Frontend Implementation Details

**pages/login/login.wxml:**
```xml
<view class="login-container">
  <view class="header">
    <text class="title">登录</text>
    <text class="subtitle">欢迎回来</text>
  </view>
  
  <view class="form">
    <view class="form-item">
      <text class="label">邮箱/手机号</text>
      <input 
        class="input"
        placeholder="请输入邮箱或手机号"
        value="{{emailOrMobile}}"
        bindinput="onEmailOrMobileInput"
      />
    </view>
    
    <view class="form-item">
      <text class="label">密码</text>
      <view class="password-input">
        <input 
          class="input"
          placeholder="请输入密码"
          password="{{!showPassword}}"
          value="{{password}}"
          bindinput="onPasswordInput"
        />
        <text class="toggle-password" bindtap="togglePassword">
          {{showPassword ? '隐藏' : '显示'}}
        </text>
      </view>
    </view>
    
    <view class="form-options">
      <checkbox-group bindchange="onRememberMeChange">
        <checkbox value="remember" checked="{{rememberMe}}" />
        <text>记住我</text>
      </checkbox-group>
    </view>
    
    <button 
      class="login-btn"
      bindtap="onLogin"
      disabled="{{loading}}"
      loading="{{loading}}"
    >
      {{loading ? '登录中...' : '登录'}}
    </button>
  </view>
  
  <view class="footer">
    <text class="link" bindtap="navigateToRegister">还没有账号？立即注册</text>
    <text class="link" bindtap="navigateToForgotPassword">忘记密码？</text>
  </view>
</view>
```

**pages/login/login.js:**
```javascript
const api = require('../../utils/request.js');

Page({
  data: {
    emailOrMobile: '',
    password: '',
    showPassword: false,
    rememberMe: false,
    loading: false
  },

  onLoad() {
    // Check if user is already logged in
    this.checkAutoLogin();
  },

  checkAutoLogin() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token && userInfo) {
      // User is already logged in, redirect to index
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },

  onEmailOrMobileInput(e) {
    this.setData({ emailOrMobile: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  togglePassword() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  onRememberMeChange(e) {
    this.setData({ rememberMe: e.detail.value.length > 0 });
  },

  async onLogin() {
    const { emailOrMobile, password } = this.data;
    
    // Validation
    if (!emailOrMobile) {
      wx.showToast({
        title: '请输入邮箱或手机号',
        icon: 'none'
      });
      return;
    }
    
    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      });
      return;
    }
    
    this.setData({ loading: true });
    
    try {
      const response = await api.post('/api/v1/auth/login', {
        emailOrMobile,
        password
      });
      
      // Store token and user info
      wx.setStorageSync('token', response.data.token);
      wx.setStorageSync('userInfo', {
        userId: response.data.userId,
        email: response.data.email,
        mobile: response.data.mobile,
        role: response.data.role
      });
      wx.setStorageSync('tokenExpiresAt', Date.now() + response.data.expiresIn * 1000);
      
      // Update global data
      const app = getApp();
      app.globalData.userInfo = wx.getStorageSync('userInfo');
      app.globalData.token = response.data.token;
      
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });
      
      // Redirect to index page
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }, 1000);
      
    } catch (error) {
      console.error('Login failed:', error);
      wx.showToast({
        title: error.message || '登录失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  navigateToRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    });
  },

  navigateToForgotPassword() {
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  }
});
```

**utils/request.js (update with token handling):**
```javascript
const BASE_URL = 'http://localhost:8080/api/v1';

function request(options) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          // Token expired or invalid
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.removeStorageSync('tokenExpiresAt');
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          });
          setTimeout(() => {
            wx.redirectTo({
              url: '/pages/login/login'
            });
          }, 1500);
          reject(new Error('登录已过期'));
        } else {
          reject(new Error(res.data.message || '请求失败'));
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

function get(url, data) {
  return request({ url, method: 'GET', data });
}

function post(url, data) {
  return request({ url, method: 'POST', data });
}

module.exports = {
  get,
  post
};
```

**app.js (update with token check):**
```javascript
App({
  globalData: {
    userInfo: null,
    token: null
  },

  onLaunch() {
    // Check token expiration on app launch
    this.checkTokenExpiration();
  },

  checkTokenExpiration() {
    const tokenExpiresAt = wx.getStorageSync('tokenExpiresAt');
    
    if (tokenExpiresAt && Date.now() >= tokenExpiresAt) {
      // Token has expired
      wx.removeStorageSync('token');
      wx.removeStorageSync('userInfo');
      wx.removeStorageSync('tokenExpiresAt');
      this.globalData.userInfo = null;
      this.globalData.token = null;
    }
  }
});
```

**app.json (add login page):**
```json
{
  "pages": [
    "pages/index/index",
    "pages/register/register",
    "pages/verify/verify",
    "pages/login/login"
  ],
  "window": {
    "navigationBarTitleText": "中考志愿填报"
  },
  "tabBar": {
    "list": [
      {
        "pagePath": "pages/index/index",
        "text": "首页"
      }
    ]
  }
}
```

**pages/login/login.wxss:**
```css
.login-container {
  min-height: 100vh;
  padding: 60rpx 40rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
}

.header {
  text-align: center;
  margin-bottom: 80rpx;
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  color: #fff;
  display: block;
  margin-bottom: 20rpx;
}

.subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
}

.form {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.1);
}

.form-item {
  margin-bottom: 30rpx;
}

.label {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.password-input {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input .input {
  flex: 1;
}

.toggle-password {
  position: absolute;
  right: 20rpx;
  font-size: 24rpx;
  color: #667eea;
  padding: 10rpx;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
  font-size: 24rpx;
  color: #666;
}

.login-btn {
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 10rpx;
  font-size: 32rpx;
  font-weight: bold;
}

.login-btn[disabled] {
  opacity: 0.6;
}

.footer {
  text-align: center;
  margin-top: 40rpx;
}

.link {
  display: block;
  font-size: 26rpx;
  color: #fff;
  margin: 10rpx 0;
  text-decoration: underline;
}
```

**pages/login/login.json:**
```json
{
  "navigationBarTitleText": "登录",
  "usingComponents": {}
}
```

### Testing Strategy

**Unit Tests:**
- Test JWT token generation with valid user
- Test JWT token validation with valid token
- Test JWT token validation with expired token
- Test JWT token validation with invalid signature
- Test password verification with correct password
- Test password verification with wrong password
- Test user status check (VERIFIED vs UNVERIFIED)
- Test rate limiting logic
- Test token extraction from Authorization header

**Integration Tests:**
- Test successful login flow
- Test login with wrong password
- Test login with non-existent user
- Test login with unverified account
- Test login with rate limit exceeded
- Test JWT token usage in subsequent requests
- Test token expiration handling
- Test concurrent login attempts
- Test SQL injection prevention
- Test XSS prevention

**Test Example:**
```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerLoginTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthService authService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
            .email("test@example.com")
            .mobile("13800138000")
            .passwordHash(passwordEncoder.encode("Test123456"))
            .status(UserStatus.VERIFIED)
            .role(UserRole.USER)
            .build();
        testUser = userRepository.save(testUser);
    }
    
    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmailOrMobile("test@example.com");
        request.setPassword("Test123456");
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("登录成功"))
            .andExpect(jsonPath("$.data.token").isNotEmpty())
            .andExpect(jsonPath("$.data.userId").value(testUser.getId()))
            .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
    
    @Test
    @DisplayName("Should fail with wrong password")
    void testLoginWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmailOrMobile("test@example.com");
        request.setPassword("WrongPassword123");
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(40001))
            .andExpect(jsonPath("$.message").value("邮箱/手机号或密码错误"));
    }
    
    @Test
    @DisplayName("Should fail with non-existent user")
    void testLoginNonExistentUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmailOrMobile("nonexistent@example.com");
        request.setPassword("Test123456");
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(40001))
            .andExpect(jsonPath("$.message").value("邮箱/手机号或密码错误"));
    }
    
    @Test
    @DisplayName("Should fail with unverified account")
    void testLoginUnverifiedAccount() throws Exception {
        // Update user to unverified status
        testUser.setStatus(UserStatus.UNVERIFIED);
        userRepository.save(testUser);
        
        LoginRequest request = new LoginRequest();
        request.setEmailOrMobile("test@example.com");
        request.setPassword("Test123456");
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(40002))
            .andExpect(jsonPath("$.message").value("账户未验证，请先验证邮箱/手机号"));
    }
}
```

### Error Handling

**Error Codes:**
- 40001: Authentication failed (wrong password or non-existent user)
- 40002: Account not verified
- 40003: Token expired
- 40004: Rate limit exceeded
- 40005: Invalid token format
- 40006: Token validation failed

**Error Messages:**
- "邮箱/手机号或密码错误" - Generic message for authentication failures
- "账户未验证，请先验证邮箱/手机号" - Unverified account
- "登录已过期，请重新登录" - Token expired
- "请求过于频繁，请稍后再试" - Rate limit exceeded
- "Token 格式错误" - Invalid token format
- "Token 验证失败" - Token validation failed

### Performance Considerations

- Use BCrypt for password verification (strength 12)
- Cache JWT validation results for same token within short window
- Implement rate limiting to prevent brute force attacks
- Use connection pooling for database operations
- Consider caching user data in Redis for frequently accessed users
- Log login attempts asynchronously to avoid blocking
- Use async/scheduled cleanup for expired tokens (if stored in DB)

### Security Considerations

- Never log passwords or JWT tokens
- Use constant-time comparison for password verification
- Implement rate limiting to prevent brute force attacks
- Use HTTPS for all API calls (enforce in production)
- Sanitize all error messages to prevent information disclosure
- Validate JWT signature on every request
- Check token expiration on every request
- Use secure random for any non-deterministic operations
- Store JWT secret in environment variables
- Implement IP-based rate limiting
- Add audit logging for all login attempts
- Use Spring Security CSRF protection for admin endpoints
- Implement proper CORS configuration
- Add security headers (X-Frame-Options, X-Content-Type-Options, etc.)

### Dependencies to Add

**pom.xml additions:**
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>32.1.3-jre</version>
</dependency>
```

### Configuration

**application.yml additions:**
```yaml
jwt:
  secret: ${JWT_SECRET:your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm}
  expiration: 86400 # 24 hours in seconds

spring:
  security:
    user:
      name: admin
      password: admin123
```

**Environment variables for production:**
```bash
JWT_SECRET=your-production-secret-key-at-least-256-bits-long
```

### Project Structure

**Backend files to create:**
- src/main/java/com/chapt003/dto/request/LoginRequest.java
- src/main/java/com/chapt003/dto/response/LoginResponse.java
- src/main/java/com/chapt003/util/JwtUtil.java
- src/main/java/com/chapt003/security/JwtAuthenticationFilter.java
- src/main/java/com/chapt003/security/SecurityConfig.java
- src/main/java/com/chapt003/security/RateLimitAspect.java
- src/main/java/com/chapt003/annotation/RateLimit.java
- src/main/java/com/chapt003/exception/AuthenticationException.java
- src/main/java/com/chapt003/exception/UnverifiedAccountException.java
- src/main/java/com/chapt003/exception/TokenExpiredException.java

**Backend files to modify:**
- src/main/java/com/chapt003/service/AuthService.java (add login method)
- src/main/java/com/chapt003/controller/AuthController.java (add login endpoint)
- src/main/java/com/chapt003/exception/GlobalExceptionHandler.java (add login-specific exceptions)
- src/main/resources/application.yml (add JWT configuration)
- pom.xml (add JWT, Spring Security, Guava dependencies)

**Frontend files to create:**
- pages/login/login.wxml
- pages/login/login.wxss
- pages/login/login.js
- pages/login/login.json

**Frontend files to modify:**
- app.json (add login page to pages array)
- app.js (add token expiration check)
- utils/request.js (add token handling and 401 error handling)

**Test files to create:**
- src/test/java/com/chapt003/util/JwtUtilTest.java
- src/test/java/com/chapt003/security/JwtAuthenticationFilterTest.java
- src/test/java/com/chapt003/controller/AuthControllerLoginTest.java
- src/test/java/com/chapt003/service/AuthServiceLoginTest.java

### References

- [architecture.md#Authentication Methods](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L500-520) - Authentication flow and JWT implementation
- [architecture.md#Security Middleware](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L521-530) - Spring Security and JWT configuration
- [architecture.md#Encryption Methods](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L540-550) - BCrypt password encryption
- [architecture.md#API Security Strategy](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L551-570) - Rate limiting and security measures
- [architecture.md#Audit Logging](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L571-580) - Security audit requirements
- [epics.md#Story 1.2](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/epics.md#L330-379) - Story requirements and acceptance criteria
- [ux-design-specification.md#Core UX Principles](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/ux-design-specification.md#L110-130) - UX design principles
- [code-review-story-1-1.md](file:///d:/opt/traswork/chapt003/_bmad-output/implementation-artifacts/code-review-story-1-1.md) - Learnings from previous story review
- [1-1-user-registration.md](file:///d:/opt/traswork/chapt003/_bmad-output/implementation-artifacts/1-1-user-registration.md) - Previous story patterns and conventions

## Development Agent Record

### Agent Model Used
Claude 4 (Sonnet)

### Debug Log References
None - Initial story creation

### Completion Notes
- Story 1.2 created with comprehensive requirements
- All learnings from Story 1.1 review incorporated
- Security best practices applied (rate limiting, audit logging, error sanitization)
- JWT authentication flow fully specified
- Constructor injection pattern specified (avoiding field injection)
- Complete testing strategy defined
- All file locations and naming conventions documented

### File List

**Backend files to create:**
- src/main/java/com/chapt003/dto/request/LoginRequest.java
- src/main/java/com/chapt003/dto/response/LoginResponse.java
- src/main/java/com/chapt003/util/JwtUtil.java
- src/main/java/com/chapt003/security/JwtAuthenticationFilter.java
- src/main/java/com/chapt003/security/SecurityConfig.java
- src/main/java/com/chapt003/security/RateLimitAspect.java
- src/main/java/com/chapt003/annotation/RateLimit.java
- src/main/java/com/chapt003/exception/AuthenticationException.java
- src/main/java/com/chapt003/exception/UnverifiedAccountException.java
- src/main/java/com/chapt003/exception/TokenExpiredException.java

**Backend files to modify:**
- src/main/java/com/chapt003/service/AuthService.java
- src/main/java/com/chapt003/controller/AuthController.java
- src/main/java/com/chapt003/exception/GlobalExceptionHandler.java
- src/main/resources/application.yml
- pom.xml

**Frontend files to create:**
- pages/login/login.wxml
- pages/login/login.wxss
- pages/login/login.js
- pages/login/login.json

**Frontend files to modify:**
- app.json
- app.js
- utils/request.js

**Test files to create:**
- src/test/java/com/chapt003/util/JwtUtilTest.java
- src/test/java/com/chapt003/security/JwtAuthenticationFilterTest.java
- src/test/java/com/chapt003/controller/AuthControllerLoginTest.java
- src/test/java/com/chapt003/service/AuthServiceLoginTest.java
