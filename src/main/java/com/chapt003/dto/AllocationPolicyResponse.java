package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationPolicyResponse {

    private Long id;
    private String city;
    private String district;
    private Integer year;
    private String policyName;
    private String policyType;
    private BigDecimal totalQuotaPercentage;
    private BigDecimal minScoreGap;
    private String description;
}
