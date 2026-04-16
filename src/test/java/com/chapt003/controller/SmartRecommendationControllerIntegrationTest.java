package com.chapt003.controller;

import com.chapt003.entity.*;
import com.chapt003.entity.enums.SchoolType;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SmartRecommendationControllerIntegrationTest {

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
    private RecommendationPreferenceRepository recommendationPreferenceRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @Autowired
    private VolunteerApplicationItemRepository volunteerApplicationItemRepository;

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
    private School testSchool1;
    private School testSchool2;

    @BeforeEach
    void setUp() {
        volunteerApplicationItemRepository.deleteAllInBatch();
        volunteerApplicationRepository.deleteAllInBatch();
        recommendationPreferenceRepository.deleteAllInBatch();
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

        testSchool1 = School.builder()
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
        testSchool1 = schoolRepository.save(testSchool1);

        testSchool2 = School.builder()
                .name("北京第二中学")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("北京市")
                .district("朝阳区")
                .admissionScoreYear1(new BigDecimal("550.00"))
                .admissionScoreYear2(new BigDecimal("545.00"))
                .admissionScoreYear3(new BigDecimal("540.00"))
                .enrollmentQuota(400)
                .applicantCount(800)
                .build();
        testSchool2 = schoolRepository.save(testSchool2);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        volunteerApplicationItemRepository.deleteAllInBatch();
        volunteerApplicationRepository.deleteAllInBatch();
        recommendationPreferenceRepository.deleteAllInBatch();
        studentScoreRepository.deleteAllInBatch();
        schoolRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void generateRecommendations_WithValidData_ShouldReturnRecommendations() throws Exception {
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

        RecommendationPreference preference = RecommendationPreference.builder()
                .user(testUser)
                .preferredDistricts("海淀区")
                .preferredSchoolTypes("KEY_HIGH_SCHOOL")
                .minProbability(30)
                .maxResults(5)
                .build();
        recommendationPreferenceRepository.save(preference);

        String requestBody = "{\"min_probability\":30,\"max_results\":5,\"show_all\":false}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray())
                .andExpect(jsonPath("$.data.recommendations.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data.total_count").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data.message").isEmpty());
    }

    @Test
    void generateRecommendations_WithoutStudentScore_ShouldReturnErrorMessage() throws Exception {
        RecommendationPreference preference = RecommendationPreference.builder()
                .user(testUser)
                .preferredDistricts("海淀区")
                .preferredSchoolTypes("KEY_HIGH_SCHOOL")
                .minProbability(30)
                .maxResults(5)
                .build();
        recommendationPreferenceRepository.save(preference);

        String requestBody = "{\"min_probability\":30,\"max_results\":5,\"show_all\":false}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray())
                .andExpect(jsonPath("$.data.recommendations.length()").value(0))
                .andExpect(jsonPath("$.data.total_count").value(0))
                .andExpect(jsonPath("$.data.message").value("请先录入学生成绩"));
    }

    @Test
    void generateRecommendations_WithoutPreference_ShouldReturnErrorMessage() throws Exception {
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

        String requestBody = "{\"min_probability\":30,\"max_results\":5,\"show_all\":false}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray())
                .andExpect(jsonPath("$.data.recommendations.length()").value(0))
                .andExpect(jsonPath("$.data.total_count").value(0))
                .andExpect(jsonPath("$.data.message").value("请先设置推荐偏好"));
    }

    @Test
    void generateRecommendations_WithNoMatchingSchools_ShouldReturnEmptyMessage() throws Exception {
        StudentScore studentScore = StudentScore.builder()
                .user(testUser)
                .chinese(new BigDecimal("40"))
                .math(new BigDecimal("35"))
                .english(new BigDecimal("38"))
                .physics(new BigDecimal("32"))
                .chemistry(new BigDecimal("30"))
                .politics(new BigDecimal("28"))
                .history(new BigDecimal("30"))
                .geography(new BigDecimal("29"))
                .biology(new BigDecimal("31"))
                .build();
        studentScoreRepository.save(studentScore);

        RecommendationPreference preference = RecommendationPreference.builder()
                .user(testUser)
                .preferredDistricts("海淀区")
                .preferredSchoolTypes("KEY_HIGH_SCHOOL")
                .minProbability(80)
                .maxResults(5)
                .build();
        recommendationPreferenceRepository.save(preference);

        String requestBody = "{\"min_probability\":80,\"max_results\":5,\"show_all\":false}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray())
                .andExpect(jsonPath("$.data.recommendations.length()").value(0))
                .andExpect(jsonPath("$.data.total_count").value(0))
                .andExpect(jsonPath("$.data.message").value(org.hamcrest.Matchers.containsString("未找到合适的推荐学校")));
    }

    @Test
    void generateRecommendations_ShowAll_ShouldReturnAllMatchingSchools() throws Exception {
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

        RecommendationPreference preference = RecommendationPreference.builder()
                .user(testUser)
                .preferredDistricts("海淀区")
                .preferredSchoolTypes("KEY_HIGH_SCHOOL")
                .minProbability(30)
                .maxResults(5)
                .build();
        recommendationPreferenceRepository.save(preference);

        String requestBody = "{\"min_probability\":30,\"max_results\":5,\"show_all\":true}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray())
                .andExpect(jsonPath("$.data.recommendations.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data.total_count").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void filterByDistrict_ShouldReturnFilteredResults() throws Exception {
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

        RecommendationPreference preference = RecommendationPreference.builder()
                .user(testUser)
                .preferredDistricts("海淀区")
                .preferredSchoolTypes("KEY_HIGH_SCHOOL")
                .minProbability(30)
                .maxResults(5)
                .build();
        recommendationPreferenceRepository.save(preference);

        String requestBody = "{\"districts\":[\"海淀区\"],\"min_probability\":30,\"max_results\":5,\"show_all\":false}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray());
    }

    @Test
    void sortByProbabilityDesc_ShouldReturnSortedResults() throws Exception {
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

        RecommendationPreference preference = RecommendationPreference.builder()
                .user(testUser)
                .preferredDistricts("海淀区")
                .preferredSchoolTypes("KEY_HIGH_SCHOOL")
                .minProbability(30)
                .maxResults(5)
                .build();
        recommendationPreferenceRepository.save(preference);

        String requestBody = "{\"min_probability\":30,\"max_results\":5,\"sort_by\":\"PROBABILITY_DESC\",\"show_all\":true}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray());
    }

    @Test
    void filterAndSortTogether_ShouldApplyBoth() throws Exception {
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

        RecommendationPreference preference = RecommendationPreference.builder()
                .user(testUser)
                .preferredDistricts("海淀区")
                .preferredSchoolTypes("KEY_HIGH_SCHOOL")
                .minProbability(30)
                .maxResults(5)
                .build();
        recommendationPreferenceRepository.save(preference);

        String requestBody = "{\"districts\":[\"海淀区\"],\"school_types\":[\"KEY_HIGH_SCHOOL\"],\"min_probability\":30,\"sort_by\":\"PROBABILITY_DESC\",\"show_all\":true}";

        mockMvc.perform(post("/v1/smart-recommendation/generate")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recommendations").isArray());
    }
}
