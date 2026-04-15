package com.chapt003.controller;

import com.chapt003.dto.StudentProfileRequest;
import com.chapt003.dto.StudentScoreRequest;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.StudentProfileRepository;
import com.chapt003.repository.StudentScoreRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

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
        studentScoreRepository.deleteAll();
        studentProfileRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("student@example.com");
        testUser.setEmail("student@example.com");
        testUser.setMobile("13900139000");
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

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        studentScoreRepository.deleteAll();
        studentProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createProfile_WithValidData_ShouldReturnProfile() throws Exception {
        StudentProfileRequest request = StudentProfileRequest.builder()
                .name("张三")
                .gender("男")
                .birthDate(LocalDate.of(2008, 5, 15))
                .city("北京市")
                .district("海淀区")
                .school("北京第一中学")
                .build();

        mockMvc.perform(post("/v1/student/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.data.gender").value("男"))
                .andExpect(jsonPath("$.data.city").value("北京市"));
    }

    @Test
    void getProfile_WhenProfileExists_ShouldReturnProfile() throws Exception {
        StudentProfileRequest request = StudentProfileRequest.builder()
                .name("李四")
                .gender("女")
                .birthDate(LocalDate.of(2009, 3, 20))
                .city("上海市")
                .district("浦东新区")
                .school("上海实验中学")
                .build();

        mockMvc.perform(post("/v1/student/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/student/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("李四"));
    }

    @Test
    void getProfile_WhenProfileNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/v1/student/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_WithValidData_ShouldReturnUpdatedProfile() throws Exception {
        StudentProfileRequest createRequest = StudentProfileRequest.builder()
                .name("王五")
                .gender("男")
                .birthDate(LocalDate.of(2008, 1, 10))
                .city("广州市")
                .district("天河区")
                .school("广州中学")
                .build();

        mockMvc.perform(post("/v1/student/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        StudentProfileRequest updateRequest = StudentProfileRequest.builder()
                .name("王五六")
                .gender("男")
                .birthDate(LocalDate.of(2008, 1, 10))
                .city("深圳市")
                .district("南山区")
                .school("深圳中学")
                .build();

        mockMvc.perform(put("/v1/student/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("王五六"))
                .andExpect(jsonPath("$.data.city").value("深圳市"));
    }

    @Test
    void deleteProfile_WhenProfileExists_ShouldSucceed() throws Exception {
        StudentProfileRequest request = StudentProfileRequest.builder()
                .name("赵六")
                .gender("男")
                .birthDate(LocalDate.of(2008, 7, 25))
                .city("成都市")
                .district("武侯区")
                .school("成都七中")
                .build();

        mockMvc.perform(post("/v1/student/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/v1/student/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/v1/student/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createScore_WithValidData_ShouldReturnScore() throws Exception {
        StudentScoreRequest request = StudentScoreRequest.builder()
                .chinese(new BigDecimal("120.50"))
                .math(new BigDecimal("135.00"))
                .english(new BigDecimal("128.75"))
                .physics(new BigDecimal("95.00"))
                .chemistry(new BigDecimal("88.50"))
                .politics(new BigDecimal("92.00"))
                .history(new BigDecimal("85.50"))
                .geography(new BigDecimal("78.00"))
                .biology(new BigDecimal("82.50"))
                .build();

        mockMvc.perform(post("/v1/student/score")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.chinese").value(120.50))
                .andExpect(jsonPath("$.data.math").value(135.00))
                .andExpect(jsonPath("$.data.totalScore").exists())
                .andExpect(jsonPath("$.data.averageScore").exists());
    }

    @Test
    void getScore_WhenScoreExists_ShouldReturnScore() throws Exception {
        StudentScoreRequest request = StudentScoreRequest.builder()
                .chinese(new BigDecimal("110.00"))
                .math(new BigDecimal("140.00"))
                .english(new BigDecimal("125.00"))
                .physics(new BigDecimal("90.00"))
                .chemistry(new BigDecimal("85.00"))
                .politics(new BigDecimal("88.00"))
                .history(new BigDecimal("82.00"))
                .geography(new BigDecimal("75.00"))
                .biology(new BigDecimal("80.00"))
                .build();

        mockMvc.perform(post("/v1/student/score")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/student/score")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.chinese").value(110.00));
    }

    @Test
    void getScore_WhenScoreNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/v1/student/score")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateScore_WithValidData_ShouldReturnUpdatedScore() throws Exception {
        StudentScoreRequest createRequest = StudentScoreRequest.builder()
                .chinese(new BigDecimal("100.00"))
                .math(new BigDecimal("120.00"))
                .english(new BigDecimal("110.00"))
                .physics(new BigDecimal("80.00"))
                .chemistry(new BigDecimal("75.00"))
                .politics(new BigDecimal("85.00"))
                .history(new BigDecimal("78.00"))
                .geography(new BigDecimal("70.00"))
                .biology(new BigDecimal("72.00"))
                .build();

        mockMvc.perform(post("/v1/student/score")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        StudentScoreRequest updateRequest = StudentScoreRequest.builder()
                .chinese(new BigDecimal("115.00"))
                .math(new BigDecimal("145.00"))
                .english(new BigDecimal("130.00"))
                .physics(new BigDecimal("98.00"))
                .chemistry(new BigDecimal("92.00"))
                .politics(new BigDecimal("95.00"))
                .history(new BigDecimal("88.00"))
                .geography(new BigDecimal("82.00"))
                .biology(new BigDecimal("85.00"))
                .build();

        mockMvc.perform(put("/v1/student/score")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.chinese").value(115.00))
                .andExpect(jsonPath("$.data.math").value(145.00));
    }

    @Test
    void deleteScore_WhenScoreExists_ShouldSucceed() throws Exception {
        StudentScoreRequest request = StudentScoreRequest.builder()
                .chinese(new BigDecimal("105.00"))
                .math(new BigDecimal("130.00"))
                .english(new BigDecimal("115.00"))
                .physics(new BigDecimal("85.00"))
                .chemistry(new BigDecimal("80.00"))
                .politics(new BigDecimal("88.00"))
                .history(new BigDecimal("80.00"))
                .geography(new BigDecimal("72.00"))
                .biology(new BigDecimal("75.00"))
                .build();

        mockMvc.perform(post("/v1/student/score")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/v1/student/score")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/v1/student/score")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProfile_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        StudentProfileRequest request = StudentProfileRequest.builder()
                .name("测试")
                .gender("男")
                .birthDate(LocalDate.of(2008, 1, 1))
                .build();

        mockMvc.perform(post("/v1/student/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createScore_WithInvalidScore_ShouldReturnBadRequest() throws Exception {
        StudentScoreRequest request = StudentScoreRequest.builder()
                .chinese(new BigDecimal("200.00"))
                .math(new BigDecimal("130.00"))
                .english(new BigDecimal("115.00"))
                .physics(new BigDecimal("85.00"))
                .chemistry(new BigDecimal("80.00"))
                .politics(new BigDecimal("88.00"))
                .history(new BigDecimal("80.00"))
                .geography(new BigDecimal("72.00"))
                .biology(new BigDecimal("75.00"))
                .build();

        mockMvc.perform(post("/v1/student/score")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
