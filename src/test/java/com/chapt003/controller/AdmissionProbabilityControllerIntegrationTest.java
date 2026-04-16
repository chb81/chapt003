package com.chapt003.controller;

import com.chapt003.dto.AdmissionProbabilityDetailResponse;
import com.chapt003.entity.School;
import com.chapt003.entity.StudentScore;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.SchoolType;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.SchoolRepository;
import com.chapt003.repository.StudentProfileRepository;
import com.chapt003.repository.StudentScoreRepository;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdmissionProbabilityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    private String token;
    private User testUser;
    private School testSchool;

    @BeforeEach
    void setUp() {
        studentScoreRepository.deleteAllInBatch();
        schoolRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        testUser = new User();
        testUser.setUsername("student@example.com");
        testUser.setEmail("student@example.com");
        testUser.setMobile("13900139001");
        testUser.setPassword(passwordEncoder.encode("Password123"));
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.VERIFIED);
        testUser.setEmailVerified(true);
        testUser.setMobileVerified(true);
        testUser = userRepository.save(testUser);
        token = jwtUtil.generateToken(testUser);

        testSchool = School.builder()
                .name("北京第一中学")
                .type(SchoolType.KEY_HIGH_SCHOOL)
                .city("北京市")
                .district("海淀区")
                .admissionScoreYear1(new BigDecimal("580.00"))
                .admissionScoreYear2(new BigDecimal("575.00"))
                .admissionScoreYear3(new BigDecimal("570.00"))
                .enrollmentQuota(500)
                .applicantCount(1000)
                .build();
        testSchool = schoolRepository.save(testSchool);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        studentScoreRepository.deleteAllInBatch();
        schoolRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void calculateProbability_WithValidData_ShouldReturnProbability() throws Exception {
        StudentScore studentScore = StudentScore.builder()
                .user(testUser)
                .chinese(new BigDecimal("95"))
                .math(new BigDecimal("98"))
                .english(new BigDecimal("92"))
                .physics(new BigDecimal("90"))
                .chemistry(new BigDecimal("88"))
                .politics(new BigDecimal("85"))
                .history(new BigDecimal("87"))
                .geography(new BigDecimal("86"))
                .biology(new BigDecimal("89"))
                .build();
        studentScoreRepository.save(studentScore);

        mockMvc.perform(get("/v1/admission-probability/calculate")
                .header("Authorization", "Bearer " + token)
                .param("schoolId", testSchool.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.probability").exists())
                .andExpect(jsonPath("$.data.student_total_score").value(810.0))
                .andExpect(jsonPath("$.data.school_admission_score_year1").value(580.0))
                .andExpect(jsonPath("$.data.school_admission_score_year2").value(575.0))
                .andExpect(jsonPath("$.data.school_admission_score_year3").value(570.0))
                .andExpect(jsonPath("$.data.admission_rate").value(0.5))
                .andExpect(jsonPath("$.data.message").exists());
    }

    @Test
    void calculateProbability_WithoutStudentScore_ShouldReturnErrorMessage() throws Exception {
        mockMvc.perform(get("/v1/admission-probability/calculate")
                .header("Authorization", "Bearer " + token)
                .param("schoolId", testSchool.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.probability").isEmpty())
                .andExpect(jsonPath("$.data.message").value("请先录入学生成绩"));
    }

    @Test
    void calculateProbability_WithInsufficientAdmissionData_ShouldReturnDataInsufficient() throws Exception {
        StudentScore studentScore = StudentScore.builder()
                .user(testUser)
                .chinese(new BigDecimal("95"))
                .math(new BigDecimal("98"))
                .english(new BigDecimal("92"))
                .physics(new BigDecimal("90"))
                .chemistry(new BigDecimal("88"))
                .politics(new BigDecimal("85"))
                .history(new BigDecimal("87"))
                .geography(new BigDecimal("86"))
                .biology(new BigDecimal("89"))
                .build();
        studentScoreRepository.save(studentScore);

        School schoolWithInsufficientData = School.builder()
                .name("北京第二中学")
                .type(SchoolType.KEY_HIGH_SCHOOL)
                .city("北京市")
                .district("海淀区")
                .admissionScoreYear1(new BigDecimal("580.00"))
                .enrollmentQuota(400)
                .applicantCount(800)
                .build();
        schoolWithInsufficientData = schoolRepository.save(schoolWithInsufficientData);

        mockMvc.perform(get("/v1/admission-probability/calculate")
                .header("Authorization", "Bearer " + token)
                .param("schoolId", schoolWithInsufficientData.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.probability").isEmpty())
                .andExpect(jsonPath("$.data.message").value("数据不足"));
    }

    @Test
    void calculateProbability_WithHighScore_ShouldReturnHighProbability() throws Exception {
        StudentScore studentScore = StudentScore.builder()
                .user(testUser)
                .chinese(new BigDecimal("100"))
                .math(new BigDecimal("100"))
                .english(new BigDecimal("100"))
                .physics(new BigDecimal("100"))
                .chemistry(new BigDecimal("100"))
                .politics(new BigDecimal("100"))
                .history(new BigDecimal("100"))
                .geography(new BigDecimal("100"))
                .biology(new BigDecimal("100"))
                .build();
        studentScoreRepository.save(studentScore);

        mockMvc.perform(get("/v1/admission-probability/calculate")
                .header("Authorization", "Bearer " + token)
                .param("schoolId", testSchool.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.probability").exists())
                .andExpect(jsonPath("$.data.probability").value(org.hamcrest.Matchers.greaterThanOrEqualTo(80)));
    }

    @Test
    void calculateProbability_WithLowScore_ShouldReturnLowProbability() throws Exception {
        StudentScore studentScore = StudentScore.builder()
                .user(testUser)
                .chinese(new BigDecimal("70"))
                .math(new BigDecimal("65"))
                .english(new BigDecimal("68"))
                .physics(new BigDecimal("62"))
                .chemistry(new BigDecimal("60"))
                .politics(new BigDecimal("58"))
                .history(new BigDecimal("60"))
                .geography(new BigDecimal("59"))
                .biology(new BigDecimal("61"))
                .build();
        studentScoreRepository.save(studentScore);

        mockMvc.perform(get("/v1/admission-probability/calculate")
                .header("Authorization", "Bearer " + token)
                .param("schoolId", testSchool.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.probability").exists())
                .andExpect(jsonPath("$.data.probability").value(org.hamcrest.Matchers.lessThan(50)));
    }
}
