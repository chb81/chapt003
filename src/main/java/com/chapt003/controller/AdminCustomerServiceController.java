package com.chapt003.controller;

import com.chapt003.dto.CustomerServiceMessageDTO;
import com.chapt003.dto.CustomerServiceSessionDTO;
import com.chapt003.entity.CustomerServiceMessage;
import com.chapt003.entity.CustomerServiceSession;
import com.chapt003.entity.User;
import com.chapt003.repository.CustomerServiceMessageRepository;
import com.chapt003.repository.CustomerServiceSessionRepository;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/customer-service")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-客服管理", description = "管理员查看/回复/关闭客服会话")
public class AdminCustomerServiceController {

    @Autowired
    private CustomerServiceSessionRepository sessionRepository;

    @Autowired
    private CustomerServiceMessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/sessions")
    @Operation(summary = "获取所有会话列表", description = "管理员查看所有客服会话，支持按状态筛选")
    public ResponseEntity<ApiResponse<Page<AdminSessionDTO>>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        Page<CustomerServiceSession> pageData;
        if (status != null && !status.isEmpty()) {
            pageData = sessionRepository.findBySessionStatusOrderByStartTimeDesc(
                    status, PageRequest.of(page, size));
        } else {
            pageData = sessionRepository.findAll(
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime")));
        }
        Page<AdminSessionDTO> response = pageData.map(this::convertSessionToDTO);
        return ResponseEntity.ok(ApiResponse.success("获取会话列表成功", response));
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "获取会话详情", description = "管理员查看会话详情及消息列表")
    public ResponseEntity<ApiResponse<AdminSessionDetailDTO>> getSessionDetail(@PathVariable Long sessionId) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        List<CustomerServiceMessage> messages = messageRepository.findBySessionIdOrderByMessageTimeAsc(sessionId);
        AdminSessionDetailDTO detail = AdminSessionDetailDTO.builder()
                .session(convertSessionToDTO(session))
                .messages(messages.stream().map(this::convertMessageToDTO).collect(Collectors.toList()))
                .build();
        return ResponseEntity.ok(ApiResponse.success("获取会话详情成功", detail));
    }

    @PostMapping("/sessions/{sessionId}/reply")
    @Operation(summary = "管理员回复消息", description = "管理员在指定会话中发送回复")
    public ResponseEntity<ApiResponse<CustomerServiceMessageDTO>> replyMessage(
            @PathVariable Long sessionId,
            @RequestParam Long adminId,
            @RequestBody String content) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在: " + adminId));
        CustomerServiceMessage message = new CustomerServiceMessage(session, admin, "AGENT_MESSAGE", content);
        message = messageRepository.save(message);
        return ResponseEntity.ok(ApiResponse.success("回复成功", convertMessageToDTO(message)));
    }

    @PutMapping("/sessions/{sessionId}/close")
    @Operation(summary = "关闭会话", description = "管理员关闭指定会话")
    public ResponseEntity<ApiResponse<AdminSessionDTO>> closeSession(
            @PathVariable Long sessionId,
            @RequestParam(required = false) String resolutionNote) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        session.closeSession(resolutionNote);
        sessionRepository.save(session);
        return ResponseEntity.ok(ApiResponse.success("会话已关闭", convertSessionToDTO(session)));
    }

    @PutMapping("/sessions/{sessionId}/assign")
    @Operation(summary = "分配客服", description = "管理员将客服分配给会话")
    public ResponseEntity<ApiResponse<AdminSessionDTO>> assignAgent(
            @PathVariable Long sessionId,
            @RequestParam Long agentId) {
        CustomerServiceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在: " + sessionId));
        session.assignAgent(agentId);
        sessionRepository.save(session);
        return ResponseEntity.ok(ApiResponse.success("分配成功", convertSessionToDTO(session)));
    }

    @GetMapping("/stats")
    @Operation(summary = "客服统计", description = "获取客服会话统计数据")
    public ResponseEntity<ApiResponse<Object>> getStats() {
        long activeCount = sessionRepository.countBySessionStatus("ACTIVE");
        long closedCount = sessionRepository.countBySessionStatus("CLOSED");
        long resolvedCount = sessionRepository.countBySessionStatus("RESOLVED");
        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("activeCount", activeCount);
        stats.put("closedCount", closedCount);
        stats.put("resolvedCount", resolvedCount);
        stats.put("totalCount", activeCount + closedCount + resolvedCount);
        return ResponseEntity.ok(ApiResponse.success("获取统计成功", stats));
    }

    private AdminSessionDTO convertSessionToDTO(CustomerServiceSession s) {
        return AdminSessionDTO.builder()
                .id(s.getId())
                .userId(s.getUser() != null ? s.getUser().getId() : null)
                .username(s.getUser() != null ? s.getUser().getUsername() : null)
                .status(s.getSessionStatus() != null ? s.getSessionStatus().name() : null)
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .agentId(s.getAgentId())
                .priority(s.getPriority())
                .category(s.getCategory())
                .resolutionNote(s.getResolutionNote())
                .build();
    }

    private CustomerServiceMessageDTO convertMessageToDTO(CustomerServiceMessage m) {
        CustomerServiceMessageDTO dto = new CustomerServiceMessageDTO();
        dto.setId(m.getId());
        dto.setSessionId(m.getSession() != null ? m.getSession().getId() : null);
        dto.setSenderId(m.getSender() != null ? m.getSender().getId() : null);
        dto.setSenderName(m.getSender() != null ? m.getSender().getUsername() : null);
        dto.setMessageType(m.getMessageType());
        dto.setContent(m.getContent());
        dto.setIsRead(m.getIsRead());
        dto.setMessageTime(m.getMessageTime());
        return dto;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AdminSessionDTO {
        private Long id;
        private Long userId;
        private String username;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long agentId;
        private Integer priority;
        private String category;
        private String resolutionNote;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AdminSessionDetailDTO {
        private AdminSessionDTO session;
        private List<CustomerServiceMessageDTO> messages;
    }
}
