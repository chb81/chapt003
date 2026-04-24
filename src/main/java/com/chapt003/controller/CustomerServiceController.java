package com.chapt003.controller;

import com.chapt003.dto.*;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.CustomerServiceSessionService;
import com.chapt003.service.CustomerServiceMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/customer-service")
@Tag(name = "在线客服", description = "在线客服会话创建、消息发送、会话管理等接口")
public class CustomerServiceController {

    @Autowired
    private CustomerServiceSessionService sessionService;

    @Autowired
    private CustomerServiceMessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<CustomerServiceSessionDTO>> createSession(
            Principal principal,
            @RequestBody CreateSessionRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        CustomerServiceSessionDTO session = sessionService.createSession(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("创建会话成功", session));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<CustomerServiceSessionDTO>>> getUserSessions(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        List<CustomerServiceSessionDTO> sessions = sessionService.getUserSessions(user.getId());
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/sessions/active")
    public ResponseEntity<ApiResponse<List<CustomerServiceSessionDTO>>> getUserActiveSessions(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        List<CustomerServiceSessionDTO> sessions = sessionService.getUserActiveSessions(user.getId());
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<CustomerServiceSessionDTO>> getSessionDetail(
            @PathVariable Long sessionId,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        CustomerServiceSessionDTO session = sessionService.getSessionDetail(sessionId, user.getId());
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @PutMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<CustomerServiceSessionDTO>> updateSession(
            @PathVariable Long sessionId,
            Principal principal,
            @RequestBody UpdateSessionRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        CustomerServiceSessionDTO session = sessionService.updateSession(sessionId, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", session));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<CustomerServiceMessageDTO>> sendMessage(
            @PathVariable Long sessionId,
            Principal principal,
            @RequestBody SendMessageRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        CustomerServiceMessageDTO message = messageService.sendMessage(sessionId, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("发送成功", message));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<List<CustomerServiceMessageDTO>>> getSessionMessages(
            @PathVariable Long sessionId,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        List<CustomerServiceMessageDTO> messages = messageService.getSessionMessages(sessionId, user.getId());
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/sessions/{sessionId}/messages/new")
    public ResponseEntity<ApiResponse<List<CustomerServiceMessageDTO>>> getNewMessages(
            @PathVariable Long sessionId,
            @RequestParam(required = false) Long lastMessageId,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        Long lastMsgId = lastMessageId != null ? lastMessageId : 0L;
        List<CustomerServiceMessageDTO> messages = messageService.getNewMessages(sessionId, user.getId(), lastMsgId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<ApiResponse<Void>> markMessageAsRead(
            @PathVariable Long messageId,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        messageService.markMessageAsRead(messageId, user.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/messages/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        User user = getUser(principal);
        long count = messageService.getUnreadMessageCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    private User getUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
