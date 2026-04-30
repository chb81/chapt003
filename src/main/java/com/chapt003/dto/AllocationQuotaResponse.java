package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationQuotaResponse {

    private Long id;
    private Long schoolId;
    private String schoolName;
    private String sourceSchoolName;
    private String sourceSchoolCity;
    private String sourceSchoolDistrict;
    private Integer year;
    private Integer quotaCount;
    private BigDecimal admissionScore;
    private BigDecimal unifiedScore;
    private BigDecimal scoreDifference;
    private String policyRule;
    private Boolean hasAllocationAdvantage;
    private String advantageDescription;
}
