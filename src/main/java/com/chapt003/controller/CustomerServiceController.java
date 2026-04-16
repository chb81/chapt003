package com.chapt003.controller;

import com.chapt003.dto.*;
import com.chapt003.entity.User;
import com.chapt003.service.CustomerServiceSessionService;
import com.chapt003.service.CustomerServiceMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客服控制器
 */
@RestController
@RequestMapping("/api/customer-service")
@CrossOrigin(origins = "*")
public class CustomerServiceController {
    
    @Autowired
    private CustomerServiceSessionService sessionService;
    
    @Autowired
    private CustomerServiceMessageService messageService;
    
    /**
     * 创建客服会话
     */
    @PostMapping("/sessions")
    public ResponseEntity<CustomerServiceSessionDTO> createSession(
            @AuthenticationPrincipal User user,
            @RequestBody CreateSessionRequest request) {
        CustomerServiceSessionDTO session = sessionService.createSession(user.getId(), request);
        return ResponseEntity.ok(session);
    }
    
    /**
     * 获取用户的会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<CustomerServiceSessionDTO>> getUserSessions(
            @AuthenticationPrincipal User user) {
        List<CustomerServiceSessionDTO> sessions = sessionService.getUserSessions(user.getId());
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * 获取用户的活跃会话
     */
    @GetMapping("/sessions/active")
    public ResponseEntity<List<CustomerServiceSessionDTO>> getUserActiveSessions(
            @AuthenticationPrincipal User user) {
        List<CustomerServiceSessionDTO> sessions = sessionService.getUserActiveSessions(user.getId());
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * 获取会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<CustomerServiceSessionDTO> getSessionDetail(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User user) {
        CustomerServiceSessionDTO session = sessionService.getSessionDetail(sessionId, user.getId());
        return ResponseEntity.ok(session);
    }
    
    /**
     * 获取待处理会话列表（供客服人员使用）
     */
    @GetMapping("/sessions/pending")
    public ResponseEntity<List<CustomerServiceSessionDTO>> getPendingSessions(
            @AuthenticationPrincipal User user) {
        if (!"CUSTOMER_SERVICE".equals(user.getRole())) {
            return ResponseEntity.status(403).build();
        }
        
        List<CustomerServiceSessionDTO> sessions = sessionService.getPendingSessions();
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * 获取客服人员的会话列表
     */
    @GetMapping("/sessions/agent")
    public ResponseEntity<List<CustomerServiceSessionDTO>> getAgentSessions(
            @AuthenticationPrincipal User user) {
        if (!"CUSTOMER_SERVICE".equals(user.getRole())) {
            return ResponseEntity.status(403).build();
        }
        
        List<CustomerServiceSessionDTO> sessions = sessionService.getAgentSessions(user.getId());
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * 更新会话状态
     */
    @PutMapping("/sessions/{sessionId}")
    public ResponseEntity<CustomerServiceSessionDTO> updateSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User user,
            @RequestBody UpdateSessionRequest request) {
        CustomerServiceSessionDTO session = sessionService.updateSession(sessionId, user.getId(), request);
        return ResponseEntity.ok(session);
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<CustomerServiceMessageDTO> sendMessage(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User user,
            @RequestBody SendMessageRequest request) {
        CustomerServiceMessageDTO message = messageService.sendMessage(sessionId, user.getId(), request);
        return ResponseEntity.ok(message);
    }
    
    /**
     * 获取会话消息列表
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<CustomerServiceMessageDTO>> getSessionMessages(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User user) {
        List<CustomerServiceMessageDTO> messages = messageService.getSessionMessages(sessionId, user.getId());
        return ResponseEntity.ok(messages);
    }
    
    /**
     * 获取会话中的新消息
     */
    @GetMapping("/sessions/{sessionId}/messages/new")
    public ResponseEntity<List<CustomerServiceMessageDTO>> getNewMessages(
            @PathVariable Long sessionId,
            @RequestParam(required = false) Long lastMessageId,
            @AuthenticationPrincipal User user) {
        Long lastMsgId = lastMessageId != null ? lastMessageId : 0L;
        List<CustomerServiceMessageDTO> messages = messageService.getNewMessages(sessionId, user.getId(), lastMsgId);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable Long messageId,
            @AuthenticationPrincipal User user) {
        messageService.markMessageAsRead(messageId, user.getId());
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取用户未读消息数量
     */
    @GetMapping("/messages/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(
            @AuthenticationPrincipal User user) {
        long count = messageService.getUnreadMessageCount(user.getId());
        return ResponseEntity.ok(count);
    }
}