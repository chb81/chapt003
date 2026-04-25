package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogListResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String details;
    private LocalDateTime createdAt;
}
