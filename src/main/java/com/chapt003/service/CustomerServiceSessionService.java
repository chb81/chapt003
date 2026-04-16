package com.chapt003.service;

import com.chapt003.dto.*;
import com.chapt003.entity.*;
import com.chapt003.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客服会话服务
 */
@Service
@Transactional
public class CustomerServiceSessionService {
    
    @Autowired
    private CustomerServiceSessionRepository sessionRepository;
    
    @Autowired
    private CustomerServiceMessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 创建新的客服会话
     */
    public CustomerServiceSessionDTO createSession(Long userId, CreateSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        CustomerServiceSession session = new CustomerServiceSession(user);
        if (request.getCategory() != null) {
            session.setCategory(request.getCategory());
        }
        if (request.getPriority() != null) {
            session.setPriority(request.getPriority());
        }
        
        sessionRepository.save(session);
        
        // 如果有初始消息，则添加消息
        if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
            CustomerServiceMessage message = new CustomerServiceMessage(
                    session, user, "USER_MESSAGE", request.getInitialMessage());
            messageRepository.save(message);
            session.addMessage(message);
        }
        
        return convertToDTO(session);
    }
    
    /**
     * 获取用户的会话列表
     */
    @Transactional(readOnly = true)
    public List<CustomerServiceSessionDTO> getUserSessions(Long userId) {
        List<CustomerServiceSession> sessions = sessionRepository.findByUserIdOrderByStartTimeDesc(userId);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户的活跃会话
     */
    @Transactional(readOnly = true)
    public List<CustomerServiceSessionDTO> getUserActiveSessions(Long userId) {
        List<CustomerServiceSession> sessions = sessionRepository.findByUserIdAndSessionStatus(userId, "ACTIVE");
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取会话详情
     */
    @Transactional(readOnly = true)
    public CustomerServiceSessionDTO getSessionDetail(Long sessionId, Long userId) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        // 验证用户权限
        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限访问此会话");
        }
        
        // 标记消息为已读
        markSessionMessagesAsRead(sessionId, userId);
        
        return convertToDTO(session);
    }
    
    /**
     * 获取待处理会话列表（供客服人员使用）
     */
    @Transactional(readOnly = true)
    public List<CustomerServiceSessionDTO> getPendingSessions() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(5);
        List<CustomerServiceSession> sessions = sessionRepository.findPendingSessions(thresholdTime);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取客服人员的会话列表
     */
    @Transactional(readOnly = true)
    public List<CustomerServiceSessionDTO> getAgentSessions(Long agentId) {
        List<CustomerServiceSession> sessions = sessionRepository.findByAgentIdOrderByStartTimeAsc(agentId);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新会话状态
     */
    public CustomerServiceSessionDTO updateSession(Long sessionId, Long userId, UpdateSessionRequest request) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        // 验证用户权限
        if (!session.getUser().getId().equals(userId) && !isAgent(userId)) {
            throw new RuntimeException("无权限更新此会话");
        }
        
        if (request.getSessionStatus() != null) {
            session.setSessionStatus(request.getSessionStatus());
            
            // 如果会话被关闭或解决，添加系统消息
            if ("CLOSED".equals(request.getSessionStatus()) || "RESOLVED".equals(request.getSessionStatus())) {
                if (request.getResolutionNote() != null) {
                    User agent = userId.equals(session.getUser().getId()) ? session.getUser() : 
                            userRepository.findById(userId).orElse(null);
                    if (agent != null) {
                        CustomerServiceMessage message = new CustomerServiceMessage(
                                session, agent, "SYSTEM_MESSAGE", 
                                "会话已" + ("CLOSED".equals(request.getSessionStatus()) ? "关闭" : "解决") + 
                                ": " + request.getResolutionNote());
                        messageRepository.save(message);
                        session.addMessage(message);
                    }
                    session.setResolutionNote(request.getResolutionNote());
                }
                if ("CLOSED".equals(request.getSessionStatus())) {
                    session.closeSession(request.getResolutionNote());
                } else if ("RESOLVED".equals(request.getSessionStatus())) {
                    session.resolveSession(request.getResolutionNote());
                }
            }
        }
        
        if (request.getAgentId() != null) {
            session.setAgentId(request.getAgentId());
        }
        if (request.getPriority() != null) {
            session.setPriority(request.getPriority());
        }
        if (request.getCategory() != null) {
            session.setCategory(request.getCategory());
        }
        
        sessionRepository.save(session);
        return convertToDTO(session);
    }
    
    /**
     * 转换为DTO
     */
    private CustomerServiceSessionDTO convertToDTO(CustomerServiceSession session) {
        CustomerServiceSessionDTO dto = new CustomerServiceSessionDTO();
        dto.setId(session.getId());
        dto.setUserId(session.getUser().getId());
        dto.setUsername(session.getUser().getUsername());
        dto.setSessionStatus(session.getSessionStatus());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setAgentId(session.getAgentId());
        dto.setAgentName(session.getAgentId() != null ? 
                userRepository.findById(session.getAgentId()).map(User::getUsername).orElse(null) : null);
        dto.setResolutionNote(session.getResolutionNote());
        dto.setPriority(session.getPriority());
        dto.setCategory(session.getCategory());
        dto.setUnreadMessageCount((int) messageRepository.countUnreadMessagesBySessionId(session.getId()));
        dto.setLastMessageTime(session.getMessages().stream()
                .map(CustomerServiceMessage::getMessageTime)
                .max(LocalDateTime::compareTo)
                .orElse(null));
        
        return dto;
    }
    
    /**
     * 标记会话消息为已读
     */
    private void markSessionMessagesAsRead(Long sessionId, Long userId) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        if (session.getUser().getId().equals(userId)) {
            messageRepository.markAllMessagesAsRead(sessionId, LocalDateTime.now());
        }
    }
    
    /**
     * 检查用户是否为客服人员
     */
    private boolean isAgent(Long userId) {
        return userRepository.findById(userId)
                .map(user -> "CUSTOMER_SERVICE".equals(user.getRole()))
                .orElse(false);
    }
}