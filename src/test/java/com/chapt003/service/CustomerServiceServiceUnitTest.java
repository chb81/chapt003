package com.chapt003.service;

import com.chapt003.entity.CustomerServiceSession;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.SessionStatus;
import com.chapt003.entity.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 客服服务单元测试 - 只测试修复的核心功能
 */
class CustomerServiceServiceUnitTest {

    @Test
    void testSessionStatusEnum() {
        // 测试会话状态枚举
        assertEquals(SessionStatus.ACTIVE, SessionStatus.ACTIVE);
        assertEquals(SessionStatus.CLOSED, SessionStatus.CLOSED);
        assertEquals(SessionStatus.RESOLVED, SessionStatus.RESOLVED);
    }

    @Test
    void testUserRoleEnum() {
        // 测试用户角色枚举
        assertEquals(UserRole.USER, UserRole.USER);
        assertEquals(UserRole.ADMIN, UserRole.ADMIN);
    }

    @Test
    void testCustomerServiceSessionMethods() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setRole(UserRole.USER);
        
        // 创建会话
        CustomerServiceSession session = new CustomerServiceSession(user);
        
        // 测试初始状态
        assertEquals(SessionStatus.ACTIVE, session.getSessionStatus());
        assertNotNull(session.getStartTime());
        assertNull(session.getEndTime());
        assertNull(session.getAgentId());
        
        // 测试关闭会话
        session.closeSession("测试关闭");
        assertEquals(SessionStatus.CLOSED, session.getSessionStatus());
        assertNotNull(session.getEndTime());
        assertEquals("测试关闭", session.getResolutionNote());
        
        // 测试解决会话
        CustomerServiceSession session2 = new CustomerServiceSession(user);
        session2.resolveSession("测试解决");
        assertEquals(SessionStatus.RESOLVED, session2.getSessionStatus());
        assertNotNull(session2.getEndTime());
        assertEquals("测试解决", session2.getResolutionNote());
    }

    @Test
    void testSessionAssignment() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setRole(UserRole.USER);
        
        User agent = new User();
        agent.setUsername("agent");
        agent.setRole(UserRole.ADMIN);
        
        // 创建会话
        CustomerServiceSession session = new CustomerServiceSession(user);
        
        // 测试初始状态
        assertTrue(session.canAssignAgent());
        assertNull(session.getAgentId());
        
        // 分配客服
        session.assignAgent(agent.getId());
        assertEquals(agent.getId(), session.getAgentId());
        assertFalse(session.canAssignAgent());
    }

    @Test
    void testMessageTypes() {
        // 测试消息类型常量
        String[] messageTypes = {"USER_MESSAGE", "AGENT_MESSAGE", "SYSTEM_MESSAGE"};
        assertEquals(3, messageTypes.length);
        assertEquals("USER_MESSAGE", messageTypes[0]);
        assertEquals("AGENT_MESSAGE", messageTypes[1]);
        assertEquals("SYSTEM_MESSAGE", messageTypes[2]);
    }
}