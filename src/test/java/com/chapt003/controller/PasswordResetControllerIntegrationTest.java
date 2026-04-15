package com.chapt003.controller;

import com.chapt003.VolunteerApplicationSystemApplication;
import com.chapt003.dto.PasswordResetSendCodeRequest;
import com.chapt003.dto.PasswordResetVerifyRequest;
import com.chapt003.dto.RegisterRequest;
import com.chapt003.dto.VerificationRequest;
import com.chapt003.repository.UserRepository;
import com.chapt003.repository.VerificationCodeRepository;
import com.chapt003.service.EmailService;
import com.chapt003.service.SmsService;
import com.chapt003.service.VerificationCodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VolunteerApplicationSystemApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordResetControllerIntegrationTest {

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
    private EmailService emailService;

    @MockBean
    private SmsService smsService;

    @MockBean
    private VerificationCodeService verificationCodeService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        doNothing().when(emailService).sendPasswordResetCode(anyString(), anyString());
        doNothing().when(smsService).sendPasswordResetCode(anyString(), anyString());
        
        // Mock Redis
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // Mock VerificationCodeService to avoid Redis dependency in tests
        when(verificationCodeService.generateAndStoreCode(anyString(), any(), anyInt()))
            .thenReturn("123456");
        when(verificationCodeService.verifyCode(anyString(), any(), eq("123456")))
            .thenReturn(true);
        when(verificationCodeService.verifyCode(anyString(), any(), argThat(code -> !"123456".equals(code))))
            .thenReturn(false);
    }

    @AfterEach
    void tearDown() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void registerAndVerifyUser(String email, String mobile, String password) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setMobile(mobile);
        registerRequest.setPassword(password);
        registerRequest.setConfirmPassword(password);

        mockMvc.perform(post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        Thread.sleep(1000); // 等待验证码生成

        String code = verificationCodeRepository.findAll().get(0).getCode();

        VerificationRequest verifyRequest = new VerificationRequest();
        verifyRequest.setEmail(email);
        verifyRequest.setCode(code);

        mockMvc.perform(post("/v1/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void sendVerificationCode_WithEmail_ShouldReturnSuccess() throws Exception {
        registerAndVerifyUser("test@example.com", "13800138000", "Password123");

        PasswordResetSendCodeRequest request = new PasswordResetSendCodeRequest();
        request.setEmail("test@example.com");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("验证码已发送"));
    }

    @Test
    void sendVerificationCode_WithMobile_ShouldReturnSuccess() throws Exception {
        registerAndVerifyUser("test@example.com", "13800138000", "Password123");

        PasswordResetSendCodeRequest request = new PasswordResetSendCodeRequest();
        request.setMobile("13800138000");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("验证码已发送"));
    }

    @Test
    void sendVerificationCode_WithNonExistentEmail_ShouldReturnError() throws Exception {
        PasswordResetSendCodeRequest request = new PasswordResetSendCodeRequest();
        request.setEmail("nonexistent@example.com");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void sendVerificationCode_WithNonExistentMobile_ShouldReturnError() throws Exception {
        PasswordResetSendCodeRequest request = new PasswordResetSendCodeRequest();
        request.setMobile("13900139000");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void sendVerificationCode_WithInvalidEmail_ShouldReturnValidationError() throws Exception {
        PasswordResetSendCodeRequest request = new PasswordResetSendCodeRequest();
        request.setEmail("invalid-email");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void sendVerificationCode_WithInvalidMobile_ShouldReturnValidationError() throws Exception {
        PasswordResetSendCodeRequest request = new PasswordResetSendCodeRequest();
        request.setMobile("12345");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetPassword_WithEmail_ShouldReturnSuccess() throws Exception {
        registerAndVerifyUser("test@example.com", "13800138000", "OldPassword123");

        PasswordResetSendCodeRequest sendCodeRequest = new PasswordResetSendCodeRequest();
        sendCodeRequest.setEmail("test@example.com");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        String code = "123456";

        PasswordResetVerifyRequest verifyRequest = new PasswordResetVerifyRequest();
        verifyRequest.setEmail("test@example.com");
        verifyRequest.setVerificationCode(code);
        verifyRequest.setNewPassword("NewPassword123");
        verifyRequest.setConfirmPassword("NewPassword123");

        mockMvc.perform(post("/v1/auth/password-reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码重置成功"));
    }

    @Test
    void resetPassword_WithMobile_ShouldReturnSuccess() throws Exception {
        registerAndVerifyUser("test@example.com", "13800138000", "OldPassword123");

        PasswordResetSendCodeRequest sendCodeRequest = new PasswordResetSendCodeRequest();
        sendCodeRequest.setMobile("13800138000");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        String code = "123456";

        PasswordResetVerifyRequest verifyRequest = new PasswordResetVerifyRequest();
        verifyRequest.setMobile("13800138000");
        verifyRequest.setVerificationCode(code);
        verifyRequest.setNewPassword("NewPassword123");
        verifyRequest.setConfirmPassword("NewPassword123");

        mockMvc.perform(post("/v1/auth/password-reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码重置成功"));
    }

    @Test
    void resetPassword_WithInvalidCode_ShouldReturnError() throws Exception {
        registerAndVerifyUser("test@example.com", "13800138000", "OldPassword123");

        PasswordResetVerifyRequest verifyRequest = new PasswordResetVerifyRequest();
        verifyRequest.setEmail("test@example.com");
        verifyRequest.setVerificationCode("000000");
        verifyRequest.setNewPassword("NewPassword123");
        verifyRequest.setConfirmPassword("NewPassword123");

        mockMvc.perform(post("/v1/auth/password-reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").value("验证码无效或已过期"));
    }

    @Test
    void resetPassword_WithPasswordMismatch_ShouldReturnError() throws Exception {
        registerAndVerifyUser("test@example.com", "13800138000", "OldPassword123");

        PasswordResetSendCodeRequest sendCodeRequest = new PasswordResetSendCodeRequest();
        sendCodeRequest.setEmail("test@example.com");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        String code = verificationCodeRepository.findAll().stream()
                .filter(vc -> vc.getEmail().equals("test@example.com"))
                .findFirst()
                .get()
                .getCode();

        PasswordResetVerifyRequest verifyRequest = new PasswordResetVerifyRequest();
        verifyRequest.setEmail("test@example.com");
        verifyRequest.setVerificationCode(code);
        verifyRequest.setNewPassword("NewPassword123");
        verifyRequest.setConfirmPassword("DifferentPassword123");

        mockMvc.perform(post("/v1/auth/password-reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").value("两次输入的密码不一致"));
    }

    @Test
    void resetPassword_WithInvalidPassword_ShouldReturnValidationError() throws Exception {
        registerAndVerifyUser("test@example.com", "13800138000", "OldPassword123");

        PasswordResetSendCodeRequest sendCodeRequest = new PasswordResetSendCodeRequest();
        sendCodeRequest.setEmail("test@example.com");

        mockMvc.perform(post("/v1/auth/password-reset/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        String code = verificationCodeRepository.findAll().stream()
                .filter(vc -> vc.getEmail().equals("test@example.com"))
                .findFirst()
                .get()
                .getCode();

        PasswordResetVerifyRequest verifyRequest = new PasswordResetVerifyRequest();
        verifyRequest.setEmail("test@example.com");
        verifyRequest.setVerificationCode(code);
        verifyRequest.setNewPassword("123");
        verifyRequest.setConfirmPassword("123");

        mockMvc.perform(post("/v1/auth/password-reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetPassword_WithNonExistentUser_ShouldReturnError() throws Exception {
        PasswordResetVerifyRequest verifyRequest = new PasswordResetVerifyRequest();
        verifyRequest.setEmail("nonexistent@example.com");
        verifyRequest.setVerificationCode("123456");
        verifyRequest.setNewPassword("NewPassword123");
        verifyRequest.setConfirmPassword("NewPassword123");

        mockMvc.perform(post("/v1/auth/password-reset/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }
}
