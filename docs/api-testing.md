# API 测试文档

## 测试概述

本文档提供了完整的API测试方案，包括单元测试、集成测试和性能测试。

## 测试环境

### 环境变量
```bash
# 测试环境配置
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# JWT配置
jwt.secret=test-secret-key-for-testing
jwt.expiration=3600000
jwt.refresh-expiration=604800000

# 微信配置
wechat.app.id=test-wechat-app-id
wechat.app.secret=test-wechat-app-secret

# 测试数据
test.email=test@example.com
test.password=Test123456
test.mobile=13800138000
```

### 测试工具
- **JUnit 5**: 单元测试框架
- **Spring Boot Test**: Spring Boot集成测试
- **Mockito**: Mock框架
- **RestAssured**: API测试
- **TestContainers**: 集成测试容器
- **H2 Database**: 内存数据库
- **Helm**: Kubernetes测试

## 测试结构

```
src/test/java/com/chapt003/
├── unit/                          # 单元测试
│   ├── service/                   # 服务层测试
│   ├── repository/               # 数据层测试
│   ├── controller/              # 控制器测试
│   ├── util/                    # 工具类测试
│   └── security/                # 安全相关测试
├── integration/                 # 集成测试
│   ├── controller/              # 控制器集成测试
│   ├── service/                 # 服务层集成测试
│   ├── database/                # 数据库集成测试
│   └── api/                     # API端到端测试
├── performance/                 # 性能测试
│   ├── benchmark/              # 基准测试
│   ├── load/                   # 负载测试
│   └── stress/                 # 压力测试
└── config/                     # 测试配置
```

## 单元测试

### 1. 认证服务测试

```java
package com.chapt003.service;

import com.chapt003.dto.LoginRequest;
import com.chapt003.dto.LoginResponse;
import com.chapt003.dto.RegisterRequest;
import com.chapt003.dto.RegisterResponse;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.repository.UserRepository;
import com.chapt003.service.AuthService;
import com.chapt003.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.ACTIVE);
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Test123456");
        registerRequest.setNickname("测试用户");
        
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Test123456");
    }
    
    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Test123456")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any())).thenReturn("test-token");
        
        // Act
        RegisterResponse response = authService.register(registerRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void registerUser_UsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> authService.register(registerRequest));
    }
    
    @Test
    void login_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Test123456", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("test-token");
        
        // Act
        LoginResponse response = authService.login(loginRequest, mock(HttpServletRequest.class));
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());
    }
    
    @Test
    void login_InvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        
        // Act & Assert
        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest, mock(HttpServletRequest.class)));
    }
}
```

### 2. 用户服务测试

```java
package com.chapt003.service;

import com.chapt003.dto.UserProfileResponse;
import com.chapt003.dto.UpdateUserProfileRequest;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserProfileService userProfileService;
    
    private User testUser;
    private UpdateUserProfileRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setMobile("13800138000");
        testUser.setNickname("测试用户");
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        
        updateRequest = new UpdateUserProfileRequest();
        updateRequest.setNickname("新昵称");
        updateRequest.setAvatarUrl("https://example.com/new-avatar.jpg");
    }
    
    @Test
    void getUserProfile_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        UserProfileResponse response = userProfileService.getUserProfile(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("测试用户", response.getNickname());
    }
    
    @Test
    void updateUserProfile_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        userProfileService.updateUserProfile(1L, updateRequest);
        
        // Assert
        verify(userRepository).save(argThat(user -> 
            user.getNickname().equals("新昵称") && 
            user.getAvatarUrl().equals("https://example.com/new-avatar.jpg")
        ));
    }
}
```

### 3. JWT工具测试

```java
package com.chapt003.util;

import com.chapt003.security.JwtUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private JwtUserDetailsService userDetailsService;
    
    @InjectMocks
    private JwtUtil jwtUtil;
    
    private String testToken;
    private UserDetails userDetails;
    private static final String SECRET = "test-secret-key";
    
    @BeforeEach
    void setUp() {
        jwtUtil.setSecret(SECRET);
        
        userDetails = org.springframework.security.core.userdetails.User
            .withUsername("testuser")
            .password("password")
            .roles("USER")
            .build();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", "USER");
        
        testToken = Jwts.builder()
            .setClaims(claims)
            .setSubject("testuser")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
    }
    
    @Test
    void generateToken_Success() {
        // Act
        String token = jwtUtil.generateToken(userDetails);
        
        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void extractUsername_Success() {
        // Act
        String username = jwtUtil.extractUsername(testToken);
        
        // Assert
        assertEquals("testuser", username);
    }
    
    @Test
    void extractExpiration_Success() {
        // Act
        Date expiration = jwtUtil.extractExpiration(testToken);
        
        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
    
    @Test
    void validateToken_Success() {
        // Arrange
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        
        // Act
        boolean isValid = jwtUtil.validateToken(testToken, userDetails);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void validateToken_Expired() {
        // Arrange
        String expiredToken = Jwts.builder()
            .setSubject("testuser")
            .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
            .setExpiration(new Date(System.currentTimeMillis() - 3600000))
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
        
        // Act & Assert
        assertThrows(TokenExpiredException.class, () -> 
            jwtUtil.validateToken(expiredToken, userDetails)
        );
    }
}
```

## 集成测试

### 1. 认证控制器集成测试

```java
package com.chapt003.integration.controller;

import com.chapt003.dto.LoginRequest;
import com.chapt003.dto.LoginResponse;
import com.chapt003.dto.RegisterRequest;
import com.chapt003.dto.RegisterResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Test123456");
        registerRequest.setNickname("测试用户");
        
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Test123456");
    }
    
    @Test
    void registerUser_Integration() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)));
        
        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("注册成功"));
        
        // Verify user exists in database
        assertTrue(userRepository.existsByUsername("testuser"));
        assertTrue(userRepository.existsByEmail("test@example.com"));
    }
    
    @Test
    void login_Integration() throws Exception {
        // Register user first
        mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)));
        
        // Login
        ResultActions result = mockMvc.perform(post("/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)));
        
        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("登录成功"))
            .andExpect(jsonPath("$.data.token").exists())
            .andExpect(jsonPath("$.data.user.username").value("testuser"));
    }
    
    @Test
    void login_InvalidCredentials_Integration() throws Exception {
        // Register user first
        mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)));
        
        // Login with wrong password
        loginRequest.setPassword("wrongpassword");
        
        ResultActions result = mockMvc.perform(post("/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)));
        
        // Assert
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value(30002));
    }
}
```

### 2. 用户控制器集成测试

```java
package com.chapt003.integration.controller;

import com.chapt003.dto.UpdateUserProfileRequest;
import com.chapt003.dto.UserProfileResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser;
    private String jwtToken;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setNickname("测试用户");
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(testUser);
        
        // Generate JWT token (simplified for testing)
        jwtToken = "test-jwt-token";
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getUserProfile_Integration() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/v1/user/profile"));
        
        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("获取用户信息成功"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.username").value("testuser"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void updateUserProfile_Integration() throws Exception {
        // Arrange
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest();
        updateRequest.setNickname("新昵称");
        updateRequest.setAvatarUrl("https://example.com/new-avatar.jpg");
        
        // Act
        ResultActions result = mockMvc.perform(put("/v1/user/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));
        
        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("资料更新成功"));
        
        // Verify update in database
        User updatedUser = userRepository.findById(1L).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("新昵称", updatedUser.getNickname());
        assertEquals("https://example.com/new-avatar.jpg", updatedUser.getAvatarUrl());
    }
}
```

### 3. 管理员控制器集成测试

```java
package com.chapt003.integration.controller;

import com.chapt003.dto.UpdateUserRoleRequest;
import com.chapt003.dto.UpdateUserStatusRequest;
import com.chapt003.dto.UserListResponse;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserAdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User adminUser;
    private User regularUser;
    private String jwtToken;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        // Create admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("encodedPassword");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(adminUser);
        
        // Create regular user
        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setUsername("testuser");
        regularUser.setEmail("test@example.com");
        regularUser.setPassword("encodedPassword");
        regularUser.setRole(UserRole.USER);
        regularUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(regularUser);
        
        jwtToken = "test-jwt-token";
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserList_Integration() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/api/admin/users"));
        
        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("获取用户列表成功"))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content.length()").value(2));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRole_Integration() throws Exception {
        // Arrange
        UpdateUserRoleRequest updateRequest = new UpdateUserRoleRequest();
        updateRequest.setRole("ADMIN");
        
        // Act
        ResultActions result = mockMvc.perform(put("/api/admin/users/2/role")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));
        
        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("更新用户角色成功"));
        
        // Verify update in database
        User updatedUser = userRepository.findById(2L).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_Integration() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(delete("/api/admin/users/2"));
        
        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("删除用户成功"));
        
        // Verify deletion in database
        assertFalse(userRepository.existsById(2L));
    }
}
```

## API 测试脚本

### 1. 使用curl进行API测试

```bash
#!/bin/bash

# 基础URL
BASE_URL="http://localhost:8080"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 1. 用户注册
echo "=== 测试用户注册 ==="
curl -X POST "$BASE_URL/v1/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser",
        "email": "test@example.com",
        "password": "Test123456",
        "nickname": "测试用户"
    }'

echo -e "\n"

# 2. 用户登录
echo "=== 测试用户登录 ==="
LOGIN_RESPONSE=$(curl -X POST "$BASE_URL/v1/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser",
        "password": "Test123456"
    }')

echo "$LOGIN_RESPONSE"
echo -e "\n"

# 3. 提取token
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | grep -o '[^"]*$')
if [ -z "$TOKEN" ]; then
    log_error "无法获取token"
    exit 1
fi
log_info "获取到token: $TOKEN"
echo -e "\n"

# 4. 获取用户信息
echo "=== 测试获取用户信息 ==="
curl -X GET "$BASE_URL/v1/user/profile" \
    -H "Authorization: Bearer $TOKEN"

echo -e "\n"

# 5. 更新用户信息
echo "=== 测试更新用户信息 ==="
curl -X PUT "$BASE_URL/v1/user/profile" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "nickname": "新昵称",
        "avatarUrl": "https://example.com/avatar.jpg"
    }'

echo -e "\n"

# 6. 管理员获取用户列表
echo "=== 测试管理员获取用户列表 ==="
curl -X GET "$BASE_URL/api/admin/users" \
    -H "Authorization: Bearer $TOKEN"

echo -e "\n"

# 7. 测试错误处理
echo "=== 测试错误处理 ==="
# 无效的用户名
curl -X POST "$BASE_URL/v1/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "nonexistent",
        "password": "wrongpassword"
    }'

echo -e "\n"

# 无效的token
curl -X GET "$BASE_URL/v1/user/profile" \
    -H "Authorization: Bearer invalid-token"

echo -e "\n"

log_info "API测试完成"
```

### 2. 使用Postman测试

#### Postman集合结构
```
Chapt003 API测试集合
├── 认证相关
│   ├── 用户注册
│   ├── 用户登录
│   ├── 微信登录
│   ├── 邮箱验证
│   └── 密码重置
├── 用户管理
│   ├── 获取用户信息
│   ├── 更新用户信息
│   ├── 验证邮箱变更
│   └── 验证手机号变更
└── 管理员功能
    ├── 获取用户列表
    ├── 获取用户详情
    ├── 更新用户角色
    ├── 更新用户状态
    └── 删除用户
```

#### 环境变量
```json
{
  "base_url": "http://localhost:8080",
  "token": "",
  "user_id": "",
  "admin_token": ""
}
```

#### 预请求脚本 (用于获取token)
```javascript
// 用户登录预请求脚本
const loginRequest = {
    "username": "testuser",
    "password": "Test123456"
};

pm.sendRequest({
    url: pm.environment.get("base_url") + "/v1/auth/login",
    method: "POST",
    header: {
        "Content-Type": "application/json"
    },
    body: loginRequest
}, function (err, res) {
    if (err === null) {
        const response = res.json();
        pm.environment.set("token", response.data.token);
        pm.environment.set("user_id", response.data.user.id);
        console.log("Token获取成功:", response.data.token);
    } else {
        console.log("Token获取失败:", err);
    }
});
```

## 性能测试

### 1. 使用JMeter进行性能测试

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0" jmeter="5.4.3">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Chapt003 API性能测试" enabled="true">
      <arguments name="TestPlan.comments" value=""/>
      <arguments name="TestPlan.functional_mode" value="false"/>
      <arguments name="TestPlan.tearDown_on_shutdown" value="true"/>
      <arguments name="TestPlan.default_config_port" value="0"/>
      <arguments name="TestPlan.serialize_threads" value="false"/>
      <arguments name="TestPlan.user_define_classpath" value=""/>
      <hashTree>
        <Arguments name="User Defined Variables" guiclass="ArgumentsPanel" testclass="Arguments" testname="用户定义变量" enabled="true">
          <collection name="Arguments.arguments" class="Arguments">
            <Arguments name="base_url" value="http://localhost:8080"/>
          </collection>
          <hashTree/>
        </Arguments>
        <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="登录性能测试" enabled="true">
          <arguments name="ThreadGroup.num_threads" value="100"/>
          <arguments name="ThreadGroup.ramp_time" value="10"/>
          <arguments name="ThreadGroup.duration" value="60"/>
          <arguments name="ThreadGroup.delay" value="0"/>
          <arguments name="ThreadGroup.scheduler" value="false"/>
          <arguments name="ThreadGroup.on_sample_error" value="continue"/>
          <collection name="ThreadGroup.arguments" class="Arguments"/>
          <hashTree>
            <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="用户登录" enabled="true">
              <elementProp name="HTTPsampler.args" class="Arguments"/>
              <elementProp name="HTTPSampler.domain" value="localhost"/>
              <elementProp name="HTTPSampler.port" value="8080"/>
              <elementProp name="HTTPSampler.protocol" value="http"/>
              <elementProp name="HTTPSampler.path" value="/v1/auth/login"/>
              <elementProp name="HTTPSampler.method" value="POST"/>
              <elementProp name="HTTPSampler.implementClass" value="HTTPSamplerBase"/>
              <elementProp name="HTTPSampler.follow_redirects" value="true"/>
              <elementProp name="HTTPSampler.auto_redirects" value="false"/>
              <elementProp name="HTTPSampler.use_keepalive" value="true"/>
              <elementProp name="HTTPSampler.doMultipart" value="false"/>
              <elementProp name="HTTPSampler.connection_timeout" value="60000"/>
              <elementProp name="HTTPSampler.response_timeout" value="60000"/>
              <elementProp name="HTTPSampler.protocol" value="http"/>
              <elementProp name="HTTPsampler.contentEncoding" value="UTF-8"/>
              <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
              <elementProp name="HTTPSampler.body" class="HTTPArgument">
                <boolProp name="HTTPArgument.always_encode">false</boolProp>
                <stringProp name="Argument.name"/>
                <stringProp name="Argument.value">{"username": "testuser", "password": "Test123456"}</stringProp>
                <stringProp name="Argument.metadata">=</stringProp>
              </elementProp>
              <elementProp name="HTTPSampler.headers" class="Header">
                <collection name="Header.header" class="Vector">
                  <elementProp name="Header.name" class="StringProperty">
                    <stringProp name="Header.name">Content-Type</stringProp>
                  </elementProp>
                  <elementProp name="Header.value" class="StringProperty">
                    <stringProp name="Header.value">application/json</stringProp>
                  </elementProp>
                </collection>
              </elementProp>
            </HTTPSamplerProxy>
            <ResultCollector guiclass="ViewResultsFullVisualizer" testclass="ResultCollector" testname="察看结果树" enabled="true">
              <boolProp name="ResultCollector.error_logging">false</boolProp>
              <boolProp name="ResultCollector.successful_only">false</boolProp>
              <boolProp name="ResultCollector.visualizeMode">false</boolProp>
            </ResultCollector>
          </hashTree>
        </ThreadGroup>
      </hashTree>
    </TestPlan>
  </hashTree>
</jmeterTestPlan>
```

### 2. 性能测试报告

```java
package com.chapt003.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ApiPerformanceSimulation extends Simulation {
  HttpBuilder httpProtocolBuilder = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json");

  ScenarioBuilder scn = scenario("API Performance Test")
    .exec(
      http("user_login")
        .post("/v1/auth/login")
        .header("Content-Type", "application/json")
        .body(StringBody(
          """
          {
            "username": "testuser",
            "password": "Test123456"
          }
          """
        ))
        .check(status().is(200))
        .check(jsonPath("$.data.token").saveAs("token"))
    )
    .exec(
      http("get_user_profile")
        .get("/v1/user/profile")
        .header("Authorization", "Bearer ${token}")
        .check(status().is(200))
    );

  {
    setUp(
      scn.injectOpen(
        rampUsersPerSec(10).to(100).during(60)
      )
    ).protocols(httpProtocolBuilder);
  }
}
```

## 测试覆盖率

### 1. 使用JaCoCo进行代码覆盖率测试

```xml
<!-- pom.xml 配置 -->
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.7</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### 2. 运行测试覆盖率报告

```bash
# 运行测试并生成覆盖率报告
mvn clean test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html
```

### 3. 覆盖率要求

- **单元测试覆盖率**: ≥ 80%
- **集成测试覆盖率**: ≥ 70%
- **分支覆盖率**: ≥ 75%
- **行覆盖率**: ≥ 85%
- **类覆盖率**: ≥ 90%

## 测试最佳实践

### 1. 测试命名规范
- 测试类名称: `[被测试类]Test`
- 测试方法名称: `test_[功能描述]_[预期结果]`
- 测试数据方法: `create_[测试数据]`

### 2. 测试数据管理
- 使用测试工厂类创建测试数据
- 使用内存数据库进行测试
- 每个测试方法独立运行
- 测试后清理测试数据

### 3. 测试环境隔离
- 使用不同的配置文件区分测试环境
- 使用内存数据库避免数据污染
- 使用Mock对象避免外部依赖

### 4. 测试报告
- 生成详细的测试报告
- 包含覆盖率信息
- 包含性能测试结果
- 包含错误分析

### 5. 持续集成
- 自动化测试执行
- 测试失败时构建失败
- 定期生成测试报告
- 测试质量门禁