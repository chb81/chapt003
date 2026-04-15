package com.chapt003.controller;

import com.chapt003.dto.UpdateUserRoleRequest;
import com.chapt003.dto.UpdateUserStatusRequest;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.repository.AuditLogRepository;
import com.chapt003.repository.LoginHistoryRepository;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

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

    private String adminToken;
    private String userToken;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        loginHistoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create Admin
        adminUser = new User();
        adminUser.setUsername("admin@example.com");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("Admin123"));
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser = userRepository.save(adminUser);
        adminToken = jwtUtil.generateToken(adminUser);

        // Create Regular User
        regularUser = new User();
        regularUser.setUsername("user@example.com");
        regularUser.setEmail("user@example.com");
        regularUser.setPassword(passwordEncoder.encode("User123"));
        regularUser.setRole(UserRole.USER);
        regularUser.setStatus(UserStatus.ACTIVE);
        regularUser = userRepository.save(regularUser);
        userToken = jwtUtil.generateToken(regularUser);

        // Mock Redis
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        auditLogRepository.deleteAll();
        loginHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getUsers_AsAdmin_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/v1/admin/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getUsers_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/v1/admin/users")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserDetail_AsAdmin_ShouldReturnDetail() throws Exception {
        mockMvc.perform(get("/v1/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }

    @Test
    void updateUserRole_AsAdmin_ShouldSucceed() throws Exception {
        UpdateUserRoleRequest request = new UpdateUserRoleRequest(UserRole.ADMIN);

        mockMvc.perform(put("/v1/admin/users/" + regularUser.getId() + "/role")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("角色更新成功"));
    }

    @Test
    void updateUserStatus_DisableUser_ShouldSucceed() throws Exception {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(UserStatus.DISABLED);

        mockMvc.perform(put("/v1/admin/users/" + regularUser.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户已禁用"));
    }

    @Test
    void deleteUser_AsAdmin_ShouldSucceed() throws Exception {
        mockMvc.perform(delete("/v1/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户已删除"));
    }

    @Test
    void updateUserStatus_SelfDisable_ShouldFail() throws Exception {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest(UserStatus.DISABLED);

        mockMvc.perform(put("/v1/admin/users/" + adminUser.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("管理员不能禁用或删除自己"));
    }
}
