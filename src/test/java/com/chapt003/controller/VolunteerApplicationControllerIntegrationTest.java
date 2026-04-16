package com.chapt003.controller;

import com.chapt003.dto.*;
import com.chapt003.entity.School;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.SchoolType;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.SchoolRepository;
import com.chapt003.repository.UserRepository;
import com.chapt003.repository.VolunteerApplicationItemRepository;
import com.chapt003.repository.VolunteerApplicationRepository;
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
import java.util.Collections;

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
class VolunteerApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @Autowired
    private VolunteerApplicationItemRepository volunteerApplicationItemRepository;

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
    private School testSchool1;

    @BeforeEach
    void setUp() {
        volunteerApplicationItemRepository.deleteAllInBatch();
        volunteerApplicationRepository.deleteAllInBatch();
        schoolRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        testUser = new User();
        testUser.setUsername("volunteer@example.com");
        testUser.setEmail("volunteer@example.com");
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
                .build();
        testSchool1 = schoolRepository.save(testSchool1);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        doNothing().when(emailService).sendPasswordResetCode(anyString(), anyString());
        when(verificationService.generateAndSendVerificationCode(anyString())).thenReturn("123456");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        volunteerApplicationItemRepository.deleteAllInBatch();
        volunteerApplicationRepository.deleteAllInBatch();
        schoolRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void createApplication_WithValidData_ShouldReturnApplication() throws Exception {
        VolunteerApplicationItemRequest itemRequest = VolunteerApplicationItemRequest.builder()
                .schoolId(testSchool1.getId())
                .priority(1)
                .build();

        VolunteerApplicationRequest request = VolunteerApplicationRequest.builder()
                .year(2024)
                .items(Collections.singletonList(itemRequest))
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.year").value(2024))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    void createApplication_WithoutAuth_ShouldReturn401() throws Exception {
        VolunteerApplicationRequest request = VolunteerApplicationRequest.builder()
                .year(2024)
                .build();

        mockMvc.perform(post("/v1/volunteer-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getApplicationById_WhenApplicationExists_ShouldReturnApplication() throws Exception {
        VolunteerApplicationRequest createRequest = VolunteerApplicationRequest.builder()
                .year(2024)
                .build();

        String createResponse = mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(get("/v1/volunteer-applications/" + applicationId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(applicationId));
    }

    @Test
    void getApplications_ShouldReturnApplicationList() throws Exception {
        VolunteerApplicationRequest request = VolunteerApplicationRequest.builder()
                .year(2024)
                .build();

        mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.applications").isArray());
    }

    @Test
    void addItem_WithValidData_ShouldReturnUpdatedApplication() throws Exception {
        VolunteerApplicationRequest createRequest = VolunteerApplicationRequest.builder()
                .year(2024)
                .build();

        String createResponse = mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        VolunteerApplicationItemRequest addItemRequest = VolunteerApplicationItemRequest.builder()
                .schoolId(testSchool1.getId())
                .priority(1)
                .build();

        mockMvc.perform(post("/v1/volunteer-applications/" + applicationId + "/items")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    void removeItem_WhenItemExists_ShouldReturnUpdatedApplication() throws Exception {
        VolunteerApplicationItemRequest itemRequest = VolunteerApplicationItemRequest.builder()
                .schoolId(testSchool1.getId())
                .priority(1)
                .build();

        VolunteerApplicationRequest createRequest = VolunteerApplicationRequest.builder()
                .year(2024)
                .items(Collections.singletonList(itemRequest))
                .build();

        String createResponse = mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(delete("/v1/volunteer-applications/" + applicationId + "/items/" + testSchool1.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    void submitApplication_WhenApplicationHasItems_ShouldSucceed() throws Exception {
        VolunteerApplicationItemRequest itemRequest = VolunteerApplicationItemRequest.builder()
                .schoolId(testSchool1.getId())
                .priority(1)
                .build();

        VolunteerApplicationRequest createRequest = VolunteerApplicationRequest.builder()
                .year(2024)
                .items(Collections.singletonList(itemRequest))
                .build();

        String createResponse = mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(post("/v1/volunteer-applications/" + applicationId + "/submit")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("SUBMITTED"));
    }

    @Test
    void submitApplication_WhenApplicationIsEmpty_ShouldReturnError() throws Exception {
        VolunteerApplicationRequest createRequest = VolunteerApplicationRequest.builder()
                .year(2024)
                .build();

        String createResponse = mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(post("/v1/volunteer-applications/" + applicationId + "/submit")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void createSimulation_WithValidData_ShouldReturnSimulation() throws Exception {
        VolunteerApplicationItemRequest itemRequest = VolunteerApplicationItemRequest.builder()
                .schoolId(testSchool1.getId())
                .priority(1)
                .build();

        VolunteerApplicationRequest request = VolunteerApplicationRequest.builder()
                .year(2024)
                .simulationName("模拟方案A")
                .items(Collections.singletonList(itemRequest))
                .build();

        mockMvc.perform(post("/v1/volunteer-applications/simulations")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("SIMULATION"))
                .andExpect(jsonPath("$.data.simulationName").value("模拟方案A"));
    }

    @Test
    void deleteApplication_WhenApplicationIsDraft_ShouldSucceed() throws Exception {
        VolunteerApplicationRequest createRequest = VolunteerApplicationRequest.builder()
                .year(2024)
                .build();

        String createResponse = mockMvc.perform(post("/v1/volunteer-applications")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(delete("/v1/volunteer-applications/" + applicationId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/v1/volunteer-applications/" + applicationId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
