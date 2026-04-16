package com.chapt003.controller;

import com.chapt003.entity.HelpDocument;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.HelpDocumentCategory;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.DocumentFavoriteRepository;
import com.chapt003.repository.DocumentFeedbackRepository;
import com.chapt003.repository.HelpDocumentRepository;
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

import java.time.LocalDateTime;

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
class HelpDocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelpDocumentRepository helpDocumentRepository;

    @Autowired
    private DocumentFavoriteRepository documentFavoriteRepository;

    @Autowired
    private DocumentFeedbackRepository documentFeedbackRepository;

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
    private HelpDocument testDocument1;
    private HelpDocument testDocument2;

    @BeforeEach
    void setUp() {
        documentFeedbackRepository.deleteAllInBatch();
        documentFavoriteRepository.deleteAllInBatch();
        helpDocumentRepository.deleteAllInBatch();
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

        testDocument1 = HelpDocument.builder()
                .title("如何注册账号")
                .category(HelpDocumentCategory.QUICK_START)
                .description("快速了解如何注册新账号")
                .content("注册账号的详细步骤...")
                .readingTime(5)
                .published(true)
                .build();
        testDocument1 = helpDocumentRepository.save(testDocument1);

        testDocument2 = HelpDocument.builder()
                .title("如何填写学生信息")
                .category(HelpDocumentCategory.STUDENT_INFO)
                .description("学生信息填写指南")
                .content("填写学生信息的详细步骤...")
                .readingTime(8)
                .published(true)
                .build();
        testDocument2 = helpDocumentRepository.save(testDocument2);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        documentFeedbackRepository.deleteAllInBatch();
        documentFavoriteRepository.deleteAllInBatch();
        helpDocumentRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void getDocumentsByCategory_WithValidCategory_ShouldReturnDocuments() throws Exception {
        mockMvc.perform(get("/v1/help-documents/category/QUICK_START")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("如何注册账号"))
                .andExpect(jsonPath("$.data[0].category").value("QUICK_START"));
    }

    @Test
    void searchDocuments_WithKeyword_ShouldReturnMatchingDocuments() throws Exception {
        mockMvc.perform(get("/v1/help-documents/search")
                .param("keyword", "注册")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("如何注册账号"));
    }

    @Test
    void searchDocuments_WithoutKeyword_ShouldReturnAllDocuments() throws Exception {
        mockMvc.perform(get("/v1/help-documents/search")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getDocumentDetail_WithValidId_ShouldReturnDetail() throws Exception {
        mockMvc.perform(get("/v1/help-documents/" + testDocument1.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testDocument1.getId()))
                .andExpect(jsonPath("$.data.title").value("如何注册账号"))
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.view_count").value(1));
    }

    @Test
    void getDocumentDetail_WithInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/v1/help-documents/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("文档不存在"));
    }

    @Test
    void toggleFavorite_ShouldToggleFavoriteStatus() throws Exception {
        mockMvc.perform(post("/v1/help-documents/" + testDocument1.getId() + "/favorite")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/v1/help-documents/favorites")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1));

        mockMvc.perform(post("/v1/help-documents/" + testDocument1.getId() + "/favorite")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/v1/help-documents/favorites")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void getFavoriteDocuments_ShouldReturnFavorites() throws Exception {
        mockMvc.perform(post("/v1/help-documents/" + testDocument1.getId() + "/favorite")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/help-documents/favorites")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].is_favorite").value(true));
    }

    @Test
    void submitFeedback_ShouldUpdateCounts() throws Exception {
        mockMvc.perform(post("/v1/help-documents/" + testDocument1.getId() + "/feedback")
                .param("isHelpful", "true")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/v1/help-documents/" + testDocument1.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.helpful_count").value(1))
                .andExpect(jsonPath("$.data.not_helpful_count").value(0));
    }
}
