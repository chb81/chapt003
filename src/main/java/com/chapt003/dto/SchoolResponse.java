package com.chapt003.dto;

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
    private String schoolCode;
    private String name;
    private String schoolType;
    private String schoolNature;
    private String type;
    private String city;
    private String district;
    private String areaCode;
    private Integer schoolRank;
    private BigDecimal admissionScoreYear1;
    private BigDecimal admissionScoreYear2;
    private BigDecimal admissionScoreYear3;
    private String description;
    private String features;
    private Integer enrollmentQuota;
    private Integer applicantCount;
    private String phone;
    private String address;
    private String schoolLevel;
    private String schoolRemark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
