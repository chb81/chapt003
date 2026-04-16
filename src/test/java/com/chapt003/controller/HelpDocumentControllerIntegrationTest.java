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
    void toggleFavorite_ShouldToggleFavoriteStatus() throws Exception {
        mockMvc.perform(post("/v1/help-documents/" + testDocument1.getId() + "/favorite")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}