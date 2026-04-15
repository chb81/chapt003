package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private String username;
    private String email;
    private String mobile;
    private String role;
    private String status;
    private boolean emailVerified;
    private boolean mobileVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private List<LoginHistoryInfo> loginHistory;
    private List<AuditLogInfo> auditLogs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginHistoryInfo {
        private LocalDateTime loginTime;
        private String ipAddress;
        private String userAgent;
        private String loginMethod;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditLogInfo {
        private Long id;
        private String action;
        private String details;
        private LocalDateTime createdAt;
        private String operatorName;
    }
}
