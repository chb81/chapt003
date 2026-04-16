package com.chapt003.controller;

import com.chapt003.dto.SchoolRequest;
import com.chapt003.entity.School;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.SchoolType;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.SchoolRepository;
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
import java.util.Arrays;

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
class SchoolControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

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

    private String userToken;
    private String adminToken;
    private User testUser;
    private User adminUser;
    private School testSchool;

    @BeforeEach
    void setUp() {
        schoolRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("user@example.com");
        testUser.setEmail("user@example.com");
        testUser.setMobile("13900139001");
        testUser.setPassword(passwordEncoder.encode("Password123"));
        testUser.setRole(UserRole.USER);
        testUser.setStatus(UserStatus.VERIFIED);
        testUser.setEmailVerified(true);
        testUser.setMobileVerified(true);
        testUser = userRepository.save(testUser);
        userToken = jwtUtil.generateToken(testUser);

        adminUser = new User();
        adminUser.setUsername("admin@example.com");
        adminUser.setEmail("admin@example.com");
        adminUser.setMobile("13900139002");
        adminUser.setPassword(passwordEncoder.encode("Password123"));
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.VERIFIED);
        adminUser.setEmailVerified(true);
        adminUser.setMobileVerified(true);
        adminUser = userRepository.save(adminUser);
        adminToken = jwtUtil.generateToken(adminUser);

        testSchool = School.builder()
                .name("北京第一中学")
                .type(SchoolType.KEY_HIGH_SCHOOL)
                .city("北京市")
                .district("海淀区")
                .admissionScoreYear1(new BigDecimal("650.00"))
                .admissionScoreYear2(new BigDecimal("645.00"))
                .admissionScoreYear3(new BigDecimal("640.00"))
                .description("北京市重点中学")
                .features("理科强校")
                .enrollmentQuota(500)
                .phone("010-12345678")
                .address("北京市海淀区中关村大街1号")
                .build();
        testSchool = schoolRepository.save(testSchool);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        doNothing().when(emailService).sendPasswordResetCode(anyString(), anyString());
        when(verificationService.generateAndSendVerificationCode(anyString())).thenReturn("123456");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        schoolRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getSchoolList_ShouldReturnSchools() throws Exception {
        mockMvc.perform(get("/v1/schools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.schools").isArray())
                .andExpect(jsonPath("$.data.schools[0].name").value("北京第一中学"));
    }

    @Test
    void getSchoolList_WithKeywordFilter_ShouldReturnMatchingSchools() throws Exception {
        mockMvc.perform(get("/v1/schools")
                .param("keyword", "第一"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.schools").isArray())
                .andExpect(jsonPath("$.data.schools[0].name").value("北京第一中学"));
    }

    @Test
    void getSchoolList_WithCityFilter_ShouldReturnMatchingSchools() throws Exception {
        mockMvc.perform(get("/v1/schools")
                .param("city", "北京市"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.schools").isArray())
                .andExpect(jsonPath("$.data.schools[0].city").value("北京市"));
    }

    @Test
    void getSchoolList_WithTypeFilter_ShouldReturnMatchingSchools() throws Exception {
        mockMvc.perform(get("/v1/schools")
                .param("type", "KEY_HIGH_SCHOOL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.schools").isArray())
                .andExpect(jsonPath("$.data.schools[0].type").value("KEY_HIGH_SCHOOL"));
    }

    @Test
    void getSchoolList_WithScoreRangeFilter_ShouldReturnMatchingSchools() throws Exception {
        mockMvc.perform(get("/v1/schools")
                .param("minScore", "600")
                .param("maxScore", "700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.schools").isArray());
    }

    @Test
    void getSchoolList_WithPagination_ShouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/v1/schools")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    void getSchoolById_WhenSchoolExists_ShouldReturnSchool() throws Exception {
        mockMvc.perform(get("/v1/schools/" + testSchool.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("北京第一中学"))
                .andExpect(jsonPath("$.data.type").value("KEY_HIGH_SCHOOL"))
                .andExpect(jsonPath("$.data.city").value("北京市"));
    }

    @Test
    void getSchoolById_WhenSchoolNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/v1/schools/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCities_ShouldReturnCityList() throws Exception {
        mockMvc.perform(get("/v1/schools/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getDistrictsByCity_ShouldReturnDistrictList() throws Exception {
        mockMvc.perform(get("/v1/schools/districts")
                .param("city", "北京市"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void createSchool_WithAdminRole_ShouldCreateSchool() throws Exception {
        SchoolRequest request = SchoolRequest.builder()
                .name("北京第二中学")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("北京市")
                .district("朝阳区")
                .admissionScoreYear1(new BigDecimal("600.00"))
                .admissionScoreYear2(new BigDecimal("595.00"))
                .admissionScoreYear3(new BigDecimal("590.00"))
                .description("北京市普通中学")
                .enrollmentQuota(400)
                .phone("010-87654321")
                .address("北京市朝阳区建国路1号")
                .build();

        mockMvc.perform(post("/v1/schools")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("北京第二中学"));
    }

    @Test
    void createSchool_WithUserRole_ShouldReturnForbidden() throws Exception {
        SchoolRequest request = SchoolRequest.builder()
                .name("北京第三中学")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("北京市")
                .district("西城区")
                .build();

        mockMvc.perform(post("/v1/schools")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSchool_WithoutToken_ShouldReturnForbidden() throws Exception {
        SchoolRequest request = SchoolRequest.builder()
                .name("北京第四中学")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("北京市")
                .district("东城区")
                .build();

        mockMvc.perform(post("/v1/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSchool_WithDuplicateName_ShouldReturnBadRequest() throws Exception {
        SchoolRequest request = SchoolRequest.builder()
                .name("北京第一中学")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("北京市")
                .district("海淀区")
                .build();

        mockMvc.perform(post("/v1/schools")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchool_WithAdminRole_ShouldUpdateSchool() throws Exception {
        SchoolRequest request = SchoolRequest.builder()
                .name("北京第一中学")
                .type(SchoolType.KEY_HIGH_SCHOOL)
                .city("北京市")
                .district("海淀区")
                .admissionScoreYear1(new BigDecimal("660.00"))
                .admissionScoreYear2(new BigDecimal("655.00"))
                .admissionScoreYear3(new BigDecimal("650.00"))
                .description("北京市重点中学（已更新）")
                .enrollmentQuota(600)
                .phone("010-11111111")
                .address("北京市海淀区中关村大街2号")
                .build();

        mockMvc.perform(put("/v1/schools/" + testSchool.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.admissionScoreYear1").value(660.00))
                .andExpect(jsonPath("$.data.description").value("北京市重点中学（已更新）"));
    }

    @Test
    void updateSchool_WithUserRole_ShouldReturnForbidden() throws Exception {
        SchoolRequest request = SchoolRequest.builder()
                .name("北京第一中学")
                .type(SchoolType.KEY_HIGH_SCHOOL)
                .city("北京市")
                .district("海淀区")
                .build();

        mockMvc.perform(put("/v1/schools/" + testSchool.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateSchool_WhenSchoolNotExists_ShouldReturn404() throws Exception {
        SchoolRequest request = SchoolRequest.builder()
                .name("不存在的学校")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("北京市")
                .district("海淀区")
                .build();

        mockMvc.perform(put("/v1/schools/99999")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSchool_WithAdminRole_ShouldDeleteSchool() throws Exception {
        mockMvc.perform(delete("/v1/schools/" + testSchool.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/v1/schools/" + testSchool.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSchool_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/v1/schools/" + testSchool.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteSchool_WhenSchoolNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/v1/schools/99999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void importSchools_WithAdminRole_ShouldImportSchools() throws Exception {
        SchoolRequest school1 = SchoolRequest.builder()
                .name("上海第一中学")
                .type(SchoolType.KEY_HIGH_SCHOOL)
                .city("上海市")
                .district("浦东新区")
                .admissionScoreYear1(new BigDecimal("640.00"))
                .build();

        SchoolRequest school2 = SchoolRequest.builder()
                .name("上海第二中学")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("上海市")
                .district("黄浦区")
                .admissionScoreYear1(new BigDecimal("580.00"))
                .build();

        mockMvc.perform(post("/v1/schools/import")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(school1, school2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void importSchools_WithUserRole_ShouldReturnForbidden() throws Exception {
        SchoolRequest school = SchoolRequest.builder()
                .name("广州第一中学")
                .type(SchoolType.KEY_HIGH_SCHOOL)
                .city("广州市")
                .district("天河区")
                .build();

        mockMvc.perform(post("/v1/schools/import")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(school))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getSchoolList_WithSortByAdmissionScore_ShouldReturnSortedSchools() throws Exception {
        School school2 = School.builder()
                .name("北京第二中学")
                .type(SchoolType.REGULAR_HIGH_SCHOOL)
                .city("北京市")
                .district("朝阳区")
                .admissionScoreYear1(new BigDecimal("550.00"))
                .build();
        schoolRepository.save(school2);

        mockMvc.perform(get("/v1/schools")
                .param("sortBy", "admissionScore")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.schools").isArray());
    }

    @Test
    void getSchoolList_WithNoMatch_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/v1/schools")
                .param("keyword", "不存在的学校名称"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.schools").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }
}
