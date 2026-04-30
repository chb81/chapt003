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
public class RiskAssessmentResponse {

    private Long planId;
    private String planName;
    private String overallRiskLevel;
    private BigDecimal overallRiskScore;
    private BigDecimal slideProbability;
    private BigDecimal safetyScore;
    private BigDecimal sprintScore;
    private BigDecimal balanceScore;
    private List<SchoolRiskDetail> schoolRisks;
    private List<String> suggestions;
    private String summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchoolRiskDetail {
        private Long schoolId;
        private String schoolName;
        private Integer order;
        private BigDecimal probability;
        private String probabilityLevel;
        private String riskTag;
        private BigDecimal confidenceInterval;
        private BigDecimal lowerBound;
        private BigDecimal upperBound;
    }
}
