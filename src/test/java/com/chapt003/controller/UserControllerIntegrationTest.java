package com.chapt003.controller;

import com.chapt003.dto.UpdateProfileRequest;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.UserRepository;
import com.chapt003.service.EmailService;
import com.chapt003.service.VerificationService;
import com.chapt003.util.JwtUtil;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    private String token;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("test@example.com");
        testUser.setEmail("test@example.com");
        testUser.setMobile("13800138000");
        testUser.setPassword(passwordEncoder.encode("Password123"));
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.VERIFIED);
        testUser.setEmailVerified(true);
        testUser.setMobileVerified(true);
        
        testUser = userRepository.save(testUser);
        token = jwtUtil.generateToken(testUser);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        doNothing().when(emailService).sendPasswordResetCode(anyString(), anyString());
        when(verificationService.generateAndSendVerificationCode(anyString())).thenReturn("123456");

        // Mock Redis
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getProfile_WithValidToken_ShouldReturnProfile() throws Exception {
        mockMvc.perform(get("/v1/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.mobile").value("13800138000"));
    }

    @Test
    void getProfile_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/user/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProfile_WithValidData_ShouldReturnUpdatedProfile() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("newname");
        request.setEmail("new@example.com");
        request.setMobile("13900139000");

        mockMvc.perform(put("/v1/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newname"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"))
                .andExpect(jsonPath("$.data.mobile").value("13900139000"));
    }

    @Test
    void updateProfile_WithDuplicateEmail_ShouldReturnError() throws Exception {
        // Create another user
        User otherUser = new User();
        otherUser.setUsername("other@example.com");
        otherUser.setEmail("other@example.com");
        otherUser.setMobile("13900139001");
        otherUser.setPassword(passwordEncoder.encode("Password123"));
        otherUser.setRole(UserRole.USER);
        otherUser.setStatus(UserStatus.VERIFIED);
        userRepository.save(otherUser);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("other@example.com");

        mockMvc.perform(put("/v1/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("邮箱")));
    }
}
