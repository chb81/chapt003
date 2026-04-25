package com.chapt003.controller;

import com.chapt003.dto.AuditLogListResponse;
import com.chapt003.entity.AuditLog;
import com.chapt003.response.ApiResponse;
import com.chapt003.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-操作日志", description = "系统操作日志查看和筛选")
public class AdminAuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogListResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) Long userId) {

        Page<AuditLog> logPage = auditLogRepository.findWithFilters(
                action, operatorId, userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        Page<AuditLogListResponse> response = logPage.map(this::convertToResponse);
        return ResponseEntity.ok(ApiResponse.success("获取操作日志成功", response));
    }

    private AuditLogListResponse convertToResponse(AuditLog log) {
        return AuditLogListResponse.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .username(log.getUser() != null ? log.getUser().getUsername() : null)
                .operatorId(log.getOperator() != null ? log.getOperator().getId() : null)
                .operatorName(log.getOperator() != null ? log.getOperator().getUsername() : "SYSTEM")
                .action(log.getAction())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
