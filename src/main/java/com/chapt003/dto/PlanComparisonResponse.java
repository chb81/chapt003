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
public class PlanComparisonResponse {

    private List<PlanSummary> plans;
    private ComparisonMetrics metrics;
    private String recommendation;
    private String recommendationReason;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanSummary {
        private Long planId;
        private String planName;
        private Integer schoolCount;
        private BigDecimal averageProbability;
        private BigDecimal totalProbability;
        private BigDecimal maxProbability;
        private BigDecimal minProbability;
        private String riskLevel;
        private BigDecimal slideProbability;
        private BigDecimal sprintScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonMetrics {
        private BigDecimal probabilityDifference;
        private BigDecimal riskDifference;
        private String saferPlan;
        private String moreAggressivePlan;
        private BigDecimal balanceScore;
    }
}
