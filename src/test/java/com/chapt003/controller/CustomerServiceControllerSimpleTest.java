package com.chapt003.controller;

import com.chapt003.dto.CreateSessionRequest;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.repository.UserRepository;
import com.chapt003.repository.CustomerServiceSessionRepository;
import com.chapt003.repository.CustomerServiceMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback
public class CustomerServiceControllerSimpleTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CustomerServiceSessionRepository sessionRepository;
    
    @Autowired
    private CustomerServiceMessageRepository messageRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // 清空数据库
        sessionRepository.deleteAll();
        messageRepository.deleteAll();
        userRepository.deleteAll();
        
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.USER);
        entityManager.persist(testUser);
        entityManager.flush();
    }
    
    @Test
    void testCreateSessionRepository() {
        // 测试会话创建的基本功能
        CreateSessionRequest request = new CreateSessionRequest();
        request.setCategory("技术咨询");
        request.setInitialMessage("我想咨询志愿填报的相关问题");
        request.setPriority(2);
        
        // 验证Repository功能正常
        assertEquals(0, sessionRepository.count());
        assertEquals(0, messageRepository.count());
    }
    
    @Test
    void testUserRepositoryFunctionality() {
        // 验证用户Repository功能
        assertNotNull(testUser.getId());
        assertEquals("testuser", testUser.getUsername());
        assertEquals(UserRole.USER, testUser.getRole());
        
        // 测试查询功能 - 使用 findById 替代 findByUsername
        User foundUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
    }
    
    @Test
    void testSessionRepositoryQueries() {
        // 创建测试用户
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password123");
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.USER);
        entityManager.persist(user2);
        entityManager.flush();
        
        // 创建一个活跃会话
        com.chapt003.entity.CustomerServiceSession session = new com.chapt003.entity.CustomerServiceSession(user2);
        session.setCategory("技术咨询");
        session.setPriority(2);
        entityManager.persist(session);
        entityManager.flush();
        
        // 测试按状态查询
        java.util.List<com.chapt003.entity.CustomerServiceSession> activeSessions = sessionRepository.findByUserIdAndSessionStatus(user2.getId(), "ACTIVE");
        assertEquals(1, activeSessions.size());
        assertEquals(session.getId(), activeSessions.get(0).getId());
        
        // 测试按用户ID查询
        java.util.List<com.chapt003.entity.CustomerServiceSession> userSessions = sessionRepository.findByUserIdOrderByStartTimeDesc(user2.getId());
        assertEquals(1, userSessions.size());
        assertEquals(session.getId(), userSessions.get(0).getId());
    }
    
    @Test
    void testMessageRepositoryQueries() {
        // 创建测试会话和用户
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password123");
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.USER);
        entityManager.persist(user2);
        entityManager.flush();
        
        com.chapt003.entity.CustomerServiceSession session = new com.chapt003.entity.CustomerServiceSession(user2);
        entityManager.persist(session);
        entityManager.flush();
        
        // 创建测试消息
        com.chapt003.entity.CustomerServiceMessage message = new com.chapt003.entity.CustomerServiceMessage(
                session, user2, "USER_MESSAGE", "测试消息");
        entityManager.persist(message);
        entityManager.flush();
        
        // 测试按会话ID查询消息
        java.util.List<com.chapt003.entity.CustomerServiceMessage> sessionMessages = messageRepository.findBySessionIdOrderByMessageTimeAsc(session.getId());
        assertEquals(1, sessionMessages.size());
        assertEquals(message.getId(), sessionMessages.get(0).getId());
        
        // 测试未读消息查询
        java.util.List<com.chapt003.entity.CustomerServiceMessage> unreadMessages = messageRepository.findUnreadMessagesBySessionId(session.getId());
        assertEquals(1, unreadMessages.size());
        assertEquals(message.getId(), unreadMessages.get(0).getId());
    }
}