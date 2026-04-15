package com.chapt003.service;

import com.chapt003.dto.WeChatLoginRequest;
import com.chapt003.dto.WeChatLoginResponse;
import com.chapt003.dto.WeChatSessionResponse;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.exception.AuthenticationException;
import com.chapt003.repository.UserRepository;
import com.chapt003.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeChatOAuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private WeChatOAuthService weChatOAuthService;

    private WeChatLoginRequest validRequest;

    private WeChatSessionResponse validSessionResponse;

    private User existingUser;

    @BeforeEach
    void setUp() {
        validRequest = new WeChatLoginRequest();
        validRequest.setCode("valid_wechat_code");

        validSessionResponse = WeChatSessionResponse.builder()
            .openid("test_openid_12345")
            .sessionKey("test_session_key")
            .build();

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("existing@example.com");
        existingUser.setMobile("13800138000");
        existingUser.setWechatOpenId("test_openid_12345");
        existingUser.setRole(UserRole.USER);
        existingUser.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void loginWithWeChat_WithNewUser_ShouldCreateUserAndReturnToken() {
        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(validSessionResponse));
        when(userRepository.findByWechatOpenId("test_openid_12345")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });
        when(jwtUtil.generateToken(any(User.class))).thenReturn("test.jwt.token");
        when(jwtUtil.extractExpiration("test.jwt.token"))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        WeChatLoginResponse response = weChatOAuthService.loginWithWeChat(validRequest);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals(2L, response.getUserId());
        assertEquals("wechat_test_o@wechat.local", response.getEmail());
        assertEquals("10000000te", response.getMobile());
        assertEquals("USER", response.getRole());
        assertTrue(response.getExpiresIn() > 0);
        assertTrue(response.isNewUser());

        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    void loginWithWeChat_WithExistingUser_ShouldReturnExistingUserToken() {
        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(validSessionResponse));
        when(userRepository.findByWechatOpenId("test_openid_12345")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(existingUser)).thenReturn("existing.jwt.token");
        when(jwtUtil.extractExpiration("existing.jwt.token"))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        WeChatLoginResponse response = weChatOAuthService.loginWithWeChat(validRequest);

        assertNotNull(response);
        assertEquals("existing.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("existing@example.com", response.getEmail());
        assertEquals("13800138000", response.getMobile());
        assertEquals("USER", response.getRole());
        assertFalse(response.isNewUser());

        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil).generateToken(existingUser);
    }

    @Test
    void loginWithWeChat_WithWeChatApiError_ShouldThrowException() {
        WeChatSessionResponse errorResponse = WeChatSessionResponse.builder()
            .errcode(40029)
            .errmsg("invalid code")
            .build();

        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(errorResponse));

        AuthenticationException exception = assertThrows(
            AuthenticationException.class,
            () -> weChatOAuthService.loginWithWeChat(validRequest)
        );

        assertEquals("微信授权失败: invalid code", exception.getMessage());
        verify(userRepository, never()).findByWechatOpenId(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void loginWithWeChat_WithNullOpenid_ShouldThrowException() {
        WeChatSessionResponse nullOpenidResponse = WeChatSessionResponse.builder()
            .openid(null)
            .sessionKey("test_session_key")
            .build();

        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(nullOpenidResponse));

        AuthenticationException exception = assertThrows(
            AuthenticationException.class,
            () -> weChatOAuthService.loginWithWeChat(validRequest)
        );

        assertEquals("获取微信用户标识失败", exception.getMessage());
        verify(userRepository, never()).findByWechatOpenId(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void loginWithWeChat_WithEmptyOpenid_ShouldThrowException() {
        WeChatSessionResponse emptyOpenidResponse = WeChatSessionResponse.builder()
            .openid("")
            .sessionKey("test_session_key")
            .build();

        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(emptyOpenidResponse));

        AuthenticationException exception = assertThrows(
            AuthenticationException.class,
            () -> weChatOAuthService.loginWithWeChat(validRequest)
        );

        assertEquals("获取微信用户标识失败", exception.getMessage());
        verify(userRepository, never()).findByWechatOpenId(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void loginWithWeChat_WithRestTemplateException_ShouldThrowException() {
        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenThrow(new RuntimeException("Network error"));

        AuthenticationException exception = assertThrows(
            AuthenticationException.class,
            () -> weChatOAuthService.loginWithWeChat(validRequest)
        );

        assertEquals("微信服务暂时不可用，请稍后重试", exception.getMessage());
        verify(userRepository, never()).findByWechatOpenId(anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void loginWithWeChat_WithExistingAdminUser_ShouldReturnAdminRole() {
        existingUser.setRole(UserRole.ADMIN);
        
        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(validSessionResponse));
        when(userRepository.findByWechatOpenId("test_openid_12345")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(existingUser)).thenReturn("admin.jwt.token");
        when(jwtUtil.extractExpiration("admin.jwt.token"))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        WeChatLoginResponse response = weChatOAuthService.loginWithWeChat(validRequest);

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
        assertFalse(response.isNewUser());
    }

    @Test
    void loginWithWeChat_WithUnionid_ShouldCreateUserCorrectly() {
        WeChatSessionResponse sessionWithUnionid = WeChatSessionResponse.builder()
            .openid("test_openid_12345")
            .sessionKey("test_session_key")
            .unionid("test_unionid_67890")
            .build();

        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(sessionWithUnionid));
        when(userRepository.findByWechatOpenId("test_openid_12345")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3L);
            return user;
        });
        when(jwtUtil.generateToken(any(User.class))).thenReturn("union.jwt.token");
        when(jwtUtil.extractExpiration("union.jwt.token"))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        WeChatLoginResponse response = weChatOAuthService.loginWithWeChat(validRequest);

        assertNotNull(response);
        assertEquals("union.jwt.token", response.getToken());
        assertTrue(response.isNewUser());

        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    void loginWithWeChat_WithDifferentOpenid_ShouldCreateDifferentUser() {
        WeChatSessionResponse differentSession = WeChatSessionResponse.builder()
            .openid("different_openid_99999")
            .sessionKey("different_session_key")
            .build();

        when(restTemplate.getForEntity(anyString(), eq(WeChatSessionResponse.class), anyMap()))
            .thenReturn(ResponseEntity.ok(differentSession));
        when(userRepository.findByWechatOpenId("different_openid_99999")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(4L);
            return user;
        });
        when(jwtUtil.generateToken(any(User.class))).thenReturn("different.jwt.token");
        when(jwtUtil.extractExpiration("different.jwt.token"))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000L));

        WeChatLoginResponse response = weChatOAuthService.loginWithWeChat(validRequest);

        assertNotNull(response);
        assertEquals("different.jwt.token", response.getToken());
        assertEquals("wechat_differ@wechat.local", response.getEmail());
        assertTrue(response.isNewUser());

        verify(userRepository).save(any(User.class));
    }
}
