package com.chapt003.integration;

import com.chapt003.dto.WeChatLoginRequest;
import com.chapt003.dto.WeChatLoginResponse;
import com.chapt003.dto.WeChatSessionResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WeChatLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private RestTemplate restTemplate;

    private static final String TEST_OPENID = "test_openid_integration_12345";
    private static final String TEST_CODE = "test_code_integration";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void wechatLogin_IntegrationFlow_NewUser_ShouldCreateUserAndReturnToken() throws Exception {
        WeChatSessionResponse sessionResponse = WeChatSessionResponse.builder()
            .openid(TEST_OPENID)
            .sessionKey("test_session_key")
            .build();

        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenReturn(ResponseEntity.ok(sessionResponse));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(TEST_CODE);

        String responseJson = mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("微信登录成功"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.isNewUser").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode responseNode = mapper.readTree(responseJson);
        String token = responseNode.get("data").get("token").asText();
        Long userId = responseNode.get("data").get("userId").asLong();

        Optional<User> createdUser = userRepository.findById(userId);
        assertTrue(createdUser.isPresent());
        assertEquals(TEST_OPENID, createdUser.get().getWechatOpenId());

        String extractedEmail = jwtUtil.extractUsername(token);
        assertTrue(extractedEmail.contains("wechat_"));
        assertTrue(extractedEmail.contains("@wechat.local"));
    }

    @Test
    void wechatLogin_IntegrationFlow_ExistingUser_ShouldReturnExistingUserToken() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setMobile("13800138000");
        existingUser.setPassword("encodedPassword");
        existingUser.setWechatOpenId(TEST_OPENID);
        existingUser.setStatus(com.chapt003.entity.enums.UserStatus.ACTIVE);
        existingUser.setRole(com.chapt003.entity.enums.UserRole.USER);
        User savedUser = userRepository.save(existingUser);

        WeChatSessionResponse sessionResponse = WeChatSessionResponse.builder()
            .openid(TEST_OPENID)
            .sessionKey("test_session_key")
            .build();

        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenReturn(ResponseEntity.ok(sessionResponse));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(TEST_CODE);

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.email").value("existing@example.com"))
                .andExpect(jsonPath("$.data.mobile").value("13800138000"))
                .andExpect(jsonPath("$.data.isNewUser").value(false));

        assertEquals(1L, userRepository.count());
        assertEquals(savedUser.getId(), userRepository.findByWechatOpenId(TEST_OPENID).get().getId());
    }

    @Test
    void wechatLogin_IntegrationFlow_WithWeChatApiError_ShouldReturnError() throws Exception {
        WeChatSessionResponse errorResponse = WeChatSessionResponse.builder()
            .errcode(40029)
            .errmsg("invalid code")
            .build();

        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenReturn(ResponseEntity.ok(errorResponse));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode("invalid_code");

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0L, userRepository.count());
    }

    @Test
    void wechatLogin_IntegrationFlow_MultipleLoginsSameUser_ShouldNotCreateDuplicateUsers() throws Exception {
        WeChatSessionResponse sessionResponse = WeChatSessionResponse.builder()
            .openid(TEST_OPENID)
            .sessionKey("test_session_key")
            .build();

        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenReturn(ResponseEntity.ok(sessionResponse));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(TEST_CODE);

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(true));

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(false));

        assertEquals(1L, userRepository.count());
    }

    @Test
    void wechatLogin_IntegrationFlow_WithNullOpenid_ShouldReturnError() throws Exception {
        WeChatSessionResponse nullOpenidResponse = WeChatSessionResponse.builder()
            .openid(null)
            .sessionKey("test_session_key")
            .build();

        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenReturn(ResponseEntity.ok(nullOpenidResponse));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(TEST_CODE);

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0L, userRepository.count());
    }

    @Test
    void wechatLogin_IntegrationFlow_WithRestTemplateException_ShouldReturnError() throws Exception {
        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenThrow(new RuntimeException("Network error"));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(TEST_CODE);

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(0L, userRepository.count());
    }

    @Test
    void wechatLogin_IntegrationFlow_AdminUserLogin_ShouldReturnAdminRole() throws Exception {
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setMobile("13900139000");
        adminUser.setPassword("encodedPassword");
        adminUser.setWechatOpenId(TEST_OPENID);
        adminUser.setStatus(com.chapt003.entity.enums.UserStatus.ACTIVE);
        adminUser.setRole(com.chapt003.entity.enums.UserRole.ADMIN);
        userRepository.save(adminUser);

        WeChatSessionResponse sessionResponse = WeChatSessionResponse.builder()
            .openid(TEST_OPENID)
            .sessionKey("test_session_key")
            .build();

        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenReturn(ResponseEntity.ok(sessionResponse));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(TEST_CODE);

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.isNewUser").value(false));
    }

    @Test
    void wechatLogin_IntegrationFlow_WithUnionid_ShouldCreateUserSuccessfully() throws Exception {
        WeChatSessionResponse sessionWithUnionid = WeChatSessionResponse.builder()
            .openid(TEST_OPENID)
            .sessionKey("test_session_key")
            .unionid("test_unionid_67890")
            .build();

        when(restTemplate.getForEntity(
            anyString(),
            eq(WeChatSessionResponse.class),
            anyMap()
        )).thenReturn(ResponseEntity.ok(sessionWithUnionid));

        WeChatLoginRequest request = new WeChatLoginRequest();
        request.setCode(TEST_CODE);

        mockMvc.perform(post("/api/v1/auth/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(true));

        Optional<User> createdUser = userRepository.findByWechatOpenId(TEST_OPENID);
        assertTrue(createdUser.isPresent());
        assertEquals(TEST_OPENID, createdUser.get().getWechatOpenId());
    }
}
