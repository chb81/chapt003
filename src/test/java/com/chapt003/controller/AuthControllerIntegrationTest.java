package com.chapt003.controller;

import com.chapt003.dto.LoginRequest;
import com.chapt003.dto.RegisterRequest;
import com.chapt003.dto.VerificationRequest;
import com.chapt003.repository.UserRepository;
import com.chapt003.repository.VerificationCodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // Mock Redis
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_WithValidData_ShouldReturnSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setMobile("13800138000");
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.mobile").value("13800138000"))
                .andExpect(jsonPath("$.data.message").value("注册成功，请查收验证邮件"));
    }

    @Test
    void register_WithPasswordMismatch_ShouldReturnError() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setMobile("13800138000");
        request.setPassword("Password123");
        request.setConfirmPassword("Different123");

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").value("两次输入的密码不一致"));
    }

    @Test
    void register_WithDuplicateEmail_ShouldReturnError() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setMobile("13800138000");
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(10001))
                .andExpect(jsonPath("$.message").value("该邮箱已被注册"));
    }

    @Test
    void register_WithInvalidEmail_ShouldReturnValidationError() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setMobile("13800138000");
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_WithInvalidMobile_ShouldReturnValidationError() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setMobile("12345");
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_WithInvalidPassword_ShouldReturnValidationError() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setMobile("13800138000");
        request.setPassword("123");
        request.setConfirmPassword("123");

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void verifyEmail_WithValidCode_ShouldReturnSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setMobile("13800138000");
        registerRequest.setPassword("Password123");
        registerRequest.setConfirmPassword("Password123");

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        String code = verificationCodeRepository.findAll().get(0).getCode();

        VerificationRequest verifyRequest = new VerificationRequest();
        verifyRequest.setEmail("test@example.com");
        verifyRequest.setCode(code);

        mockMvc.perform(post("/v1/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("邮箱验证成功"));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccess() throws Exception {
        // First register and verify
        verifyEmail_WithValidCode_ShouldReturnSuccess();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmailOrMobile("test@example.com");
        loginRequest.setPassword("Password123");

        mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
}
