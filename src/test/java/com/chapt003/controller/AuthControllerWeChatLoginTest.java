package com.chapt003.controller;

import com.chapt003.dto.WeChatLoginRequest;
import com.chapt003.dto.WeChatLoginResponse;
import com.chapt003.service.AuthService;
import com.chapt003.service.WeChatOAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerWeChatLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private WeChatOAuthService weChatOAuthService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @Test
    void wechatLogin_WithValidCode_ShouldReturnSuccess() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("valid_wechat_code");

        WeChatLoginResponse serviceResponse = WeChatLoginResponse.builder()
            .token("test.jwt.token")
            .userId(1L)
            .email("test@example.com")
            .mobile("13800138000")
            .role("USER")
            .expiresIn(86400000L)
            .isNewUser(false)
            .build();

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenReturn(serviceResponse);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("微信登录成功"))
                .andExpect(jsonPath("$.data.token").value("test.jwt.token"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.mobile").value("13800138000"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.expiresIn").value(86400000L))
                .andExpect(jsonPath("$.data.isNewUser").value(false));
    }

    @Test
    void wechatLogin_WithNewUser_ShouldReturnIsNewUserTrue() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("new_user_code");

        WeChatLoginResponse serviceResponse = WeChatLoginResponse.builder()
            .token("new.user.token")
            .userId(2L)
            .email("wechat_newuser@wechat.local")
            .mobile("10000000ne")
            .role("USER")
            .expiresIn(86400000L)
            .isNewUser(true)
            .build();

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenReturn(serviceResponse);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("微信登录成功"))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.isNewUser").value(true));
    }

    @Test
    void wechatLogin_WithAdminUser_ShouldReturnAdminRole() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("admin_code");

        WeChatLoginResponse serviceResponse = WeChatLoginResponse.builder()
            .token("admin.jwt.token")
            .userId(1L)
            .email("admin@example.com")
            .mobile("13900139000")
            .role("ADMIN")
            .expiresIn(86400000L)
            .isNewUser(false)
            .build();

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenReturn(serviceResponse);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void wechatLogin_WithEmptyCode_ShouldReturnValidationError() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("");

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void wechatLogin_WithNullCode_ShouldReturnValidationError() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(null);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void wechatLogin_WithServiceException_ShouldReturnErrorResponse() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("invalid_code");

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenThrow(new com.chapt003.exception.AuthenticationException("微信授权失败"));

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wechatLogin_WithLongExpiresIn_ShouldReturnCorrectValue() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("long_token_code");

        long longExpiresIn = 604800000L;

        WeChatLoginResponse serviceResponse = WeChatLoginResponse.builder()
            .token("long.jwt.token")
            .userId(1L)
            .email("test@example.com")
            .mobile("13800138000")
            .role("USER")
            .expiresIn(longExpiresIn)
            .isNewUser(false)
            .build();

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenReturn(serviceResponse);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.expiresIn").value(longExpiresIn));
    }

    @Test
    void wechatLogin_WithValidCodeAndNullMobile_ShouldReturnSuccess() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("valid_code_no_mobile");

        WeChatLoginResponse serviceResponse = WeChatLoginResponse.builder()
            .token("test.jwt.token")
            .userId(3L)
            .email("test@example.com")
            .mobile(null)
            .role("USER")
            .expiresIn(86400000L)
            .isNewUser(false)
            .build();

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenReturn(serviceResponse);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.mobile").isEmpty());
    }

    @Test
    void wechatLogin_WithValidCodeAndNullEmail_ShouldReturnSuccess() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("valid_code_no_email");

        WeChatLoginResponse serviceResponse = WeChatLoginResponse.builder()
            .token("test.jwt.token")
            .userId(4L)
            .email(null)
            .mobile("13800138000")
            .role("USER")
            .expiresIn(86400000L)
            .isNewUser(false)
            .build();

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenReturn(serviceResponse);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.email").isEmpty());
    }

    @Test
    void wechatLogin_WithValidCodeAndDifferentRole_ShouldReturnCorrectRole() throws Exception {
        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("manager_code");

        WeChatLoginResponse serviceResponse = WeChatLoginResponse.builder()
            .token("manager.jwt.token")
            .userId(5L)
            .email("manager@example.com")
            .mobile("13700137000")
            .role("MANAGER")
            .expiresIn(86400000L)
            .isNewUser(false)
            .build();

        when(weChatOAuthService.loginWithWeChat(any(WeChatLoginRequest.class)))
            .thenReturn(serviceResponse);

        mockMvc.perform(post("/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("MANAGER"));
    }
}
