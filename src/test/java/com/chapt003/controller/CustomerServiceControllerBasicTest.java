package com.chapt003.controller;

import com.chapt003.entity.CustomerServiceSession;
import com.chapt003.entity.CustomerServiceMessage;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.repository.CustomerServiceSessionRepository;
import com.chapt003.repository.CustomerServiceMessageRepository;
import com.chapt003.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceControllerBasicTest {

    @Autowired
    private CustomerServiceSessionRepository sessionRepository;
    
    @Autowired
    private CustomerServiceMessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Test
    void testContextLoads() {
        // 测试基本配置加载
        assertNotNull(sessionRepository);
        assertNotNull(messageRepository);
        assertNotNull(userRepository);
    }

    @Test
    void testRepositoryBasicFunctionality() {
        // 测试Repository基本功能
        assertEquals(0, sessionRepository.count());
        assertEquals(0, messageRepository.count());
        assertEquals(0, userRepository.count());
        
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRole(UserRole.USER);
        
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        
        // 创建会话
        CustomerServiceSession session = new CustomerServiceSession(savedUser);
        session.setCategory("技术咨询");
        session.setPriority(1);
        
        CustomerServiceSession savedSession = sessionRepository.save(session);
        assertNotNull(savedSession.getId());
        assertEquals(1, sessionRepository.count());
        
        // 创建消息
        CustomerServiceMessage message = new CustomerServiceMessage(
            savedSession, savedUser, "USER_MESSAGE", "测试消息");
        
        CustomerServiceMessage savedMessage = messageRepository.save(message);
        assertNotNull(savedMessage.getId());
        assertEquals(1, messageRepository.count());
    }
}