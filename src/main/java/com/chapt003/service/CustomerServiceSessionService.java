package com.chapt003.service;

import com.chapt003.dto.*;
import com.chapt003.entity.*;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.SessionStatus;
import com.chapt003.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
     * 获取客服人员可以访问的所有会话（包括自己负责的和待处理的）
     */
    @Transactional(readOnly = true)
    public List<CustomerServiceSessionDTO> getAccessibleSessions(Long agentId) {
        List<CustomerServiceSessionDTO> result = new ArrayList<>();
        
        // 获取自己负责的会话
        List<CustomerServiceSessionDTO> agentSessions = getAgentSessions(agentId);
        result.addAll(agentSessions);
        
        // 获取待处理的会话（可以分配的）
        List<CustomerServiceSessionDTO> pendingSessions = getPendingSessions();
        result.addAll(pendingSessions);
        
        // 去重并按时间排序
        return result.stream()
                .distinct()
                .sorted((s1, s2) -> s2.getLastMessageTime().compareTo(s1.getLastMessageTime()))
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取会话消息
     */
    @Transactional(readOnly = true)
    public PageResult<CustomerServiceMessageDTO> getSessionMessages(Long sessionId, Long userId, com.chapt003.dto.PageRequest pageRequest) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        // 验证用户权限
        validateUserPermission(userId, session);
        
        // 分页获取消息
        List<CustomerServiceMessage> messages = messageRepository.findBySessionIdOrderByMessageTimeAsc(sessionId);
        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getSize(), messages.size());
        List<CustomerServiceMessage> pagedMessages = messages.subList(start, end);
        
        // 转换为DTO
        List<CustomerServiceMessageDTO> messageDTOs = pagedMessages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
        
        // 返回分页结果
        return new PageResult<>(messageDTOs, pageRequest.getPage(), pageRequest.getSize(), messages.size());
    }
    
    /**
     * 转换消息为DTO
     */
    private CustomerServiceMessageDTO convertToMessageDTO(CustomerServiceMessage message) {
        CustomerServiceMessageDTO dto = new CustomerServiceMessageDTO();
        dto.setId(message.getId());
        dto.setSessionId(message.getSession().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getUsername());
        dto.setSenderRole(message.getSender().getRole().name());
        dto.setMessageType(message.getMessageType());
        dto.setContent(message.getContent());
        dto.setIsRead(message.getIsRead());
        dto.setReadTime(message.getReadTime());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        dto.setMessageTime(message.getMessageTime());
        
        return dto;
    }
    
    /**
     * 更新会话状态
     */
    public CustomerServiceSessionDTO updateSession(Long sessionId, Long userId, UpdateSessionRequest request) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        // 验证用户权限
        validateUserPermission(userId, session);
        
        if (request.getSessionStatus() != null) {
            // 使用枚举值
            SessionStatus status = SessionStatus.valueOf(request.getSessionStatus());
            session.setSessionStatus(status);
            
            // 如果会话被关闭或解决，添加系统消息
            if (status == SessionStatus.CLOSED || status == SessionStatus.RESOLVED) {
                if (request.getResolutionNote() != null) {
                    User agent = userId.equals(session.getUser().getId()) ? session.getUser() : 
                            userRepository.findById(userId).orElse(null);
                    if (agent != null) {
                        CustomerServiceMessage message = new CustomerServiceMessage(
                                session, agent, "SYSTEM_MESSAGE", 
                                "会话已" + (status == SessionStatus.CLOSED ? "关闭" : "解决") + 
                                ": " + request.getResolutionNote());
                        messageRepository.save(message);
                        session.addMessage(message);
                    }
                    session.setResolutionNote(request.getResolutionNote());
                }
                if (status == SessionStatus.CLOSED) {
                    session.closeSession(request.getResolutionNote());
                } else if (status == SessionStatus.RESOLVED) {
                    session.resolveSession(request.getResolutionNote());
                }
            }
        }
        
        if (request.getAgentId() != null) {
            // 使用线程安全的会话分配方法
            synchronized (session) {
                if (!session.canAssignAgent()) {
                    throw new RuntimeException("会话无法分配给客服：会话状态不正确或已被分配");
                }
                session.assignAgent(request.getAgentId());
            }
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
        dto.setSessionStatus(session.getSessionStatus().name());
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
     * 验证用户权限
     */
    private void validateUserPermission(Long userId, CustomerServiceSession session) {
        // 用户可以访问自己的会话
        if (session.getUser().getId().equals(userId)) {
            return;
        }
        
        // 客服人员只能访问分配给自己的会话
        if (isAgent(userId) && session.getAgentId() != null && session.getAgentId().equals(userId)) {
            return;
        }
        
        throw new RuntimeException("无权限访问此会话");
    }
    
    /**
     * 标记会话消息为已读
     */
    private void markSessionMessagesAsRead(Long sessionId, Long userId) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        validateUserPermission(userId, session);
        
        if (session.getUser().getId().equals(userId)) {
            messageRepository.markAllMessagesAsRead(sessionId, LocalDateTime.now());
        }
    }
    
    /**
     * 检查用户是否为客服人员
     */
    private boolean isAgent(Long userId) {
        return userRepository.findById(userId)
                .map(user -> UserRole.CUSTOMER_SERVICE.equals(user.getRole()))
                .orElse(false);
    }
}