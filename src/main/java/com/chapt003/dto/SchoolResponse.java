package com.chapt003.dto;

import com.chapt003.entity.enums.SchoolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolResponse {
    private Long id;
    private String name;
    private SchoolType type;
    private String city;
    private String district;
    private BigDecimal admissionScoreYear1;
    private BigDecimal admissionScoreYear2;
    private BigDecimal admissionScoreYear3;
    private String description;
    private String features;
    private Integer enrollmentQuota;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
