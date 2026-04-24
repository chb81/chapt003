package com.chapt003.service;

import com.chapt003.dto.*;
import com.chapt003.entity.*;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客服消息服务
 */
@Service
@Transactional
public class CustomerServiceMessageService {
    
    @Autowired
    private CustomerServiceMessageRepository messageRepository;
    
    @Autowired
    private CustomerServiceSessionRepository sessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 发送消息
     */
    public CustomerServiceMessageDTO sendMessage(Long sessionId, Long userId, SendMessageRequest request) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        // 验证用户权限
        validateUserPermission(userId, session);
        
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        CustomerServiceMessage message = new CustomerServiceMessage(
                session, sender, 
                getUserRole(sender.getRole()) + "_MESSAGE",
                request.getContent());
        
        if (request.getAttachmentUrl() != null) {
            message.setAttachmentUrl(request.getAttachmentUrl());
        }
        
        messageRepository.save(message);
        session.addMessage(message);
        
        return convertToDTO(message);
    }
    
    /**
     * 获取会话消息列表
     */
    @Transactional(readOnly = true)
    public List<CustomerServiceMessageDTO> getSessionMessages(Long sessionId, Long userId) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        // 验证用户权限
        validateUserPermission(userId, session);
        
        // 标记消息为已读
        if (session.getUser().getId().equals(userId)) {
            markSessionMessagesAsRead(sessionId, userId);
        }
        
        List<CustomerServiceMessage> messages = messageRepository.findBySessionIdOrderByMessageTimeAsc(sessionId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取会话中的新消息
     */
    @Transactional(readOnly = true)
    public List<CustomerServiceMessageDTO> getNewMessages(Long sessionId, Long userId, Long lastMessageId) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        // 验证用户权限
        validateUserPermission(userId, session);
        
        // 获取最后一条消息的时间
        LocalDateTime lastMessageTime = messageRepository.findById(lastMessageId)
                .map(CustomerServiceMessage::getMessageTime)
                .orElse(LocalDateTime.now().minusDays(1));
        
        List<CustomerServiceMessage> messages = messageRepository.findNewMessagesAfterTime(sessionId, lastMessageTime);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 标记消息为已读
     */
    public void markMessageAsRead(Long messageId, Long userId) {
        // 验证用户存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        CustomerServiceMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        // 验证用户权限
        validateUserPermission(userId, message.getSession());
        
        // 只有消息发送者才能标记为已读
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("只有消息发送者才能标记消息为已读");
        }
        
        message.markAsRead();
        messageRepository.save(message);
    }
    
    /**
     * 获取用户未读消息数量
     */
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessagesBySessionId(null); // 需要修改Repository支持按用户统计
    }
    
    /**
     * 转换为DTO
     */
    private CustomerServiceMessageDTO convertToDTO(CustomerServiceMessage message) {
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
     * 验证用户权限
     */
    private void validateUserPermission(Long userId, CustomerServiceSession session) {
        // 用户可以访问自己的会话
        if (session.getUser().getId().equals(userId)) {
            return;
        }
        
        // 客服人员只能访问分配给自己的会话
        if (isCustomerServiceAgent(userId) && session.getAgentId() != null && session.getAgentId().equals(userId)) {
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
     * 获取用户角色对应的消息类型
     */
    private String getUserRole(UserRole role) {
        if (UserRole.CUSTOMER_SERVICE.equals(role)) {
            return "AGENT";
        }
        return "USER";
    }
    
    /**
     * 检查用户是否为客服人员
     */
    private boolean isCustomerServiceAgent(Long userId) {
        return userRepository.findById(userId)
                .map(user -> UserRole.ADMIN.equals(user.getRole()))
                .orElse(false);
    }
}