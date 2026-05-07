package com.chapt003.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionProbabilityDetailResponse {

    @JsonProperty("student_total_score")
    private BigDecimal studentTotalScore;

    @JsonProperty("student_rank")
    private Integer studentRank;

    @JsonProperty("school_admission_score_year1")
    private BigDecimal schoolAdmissionScoreYear1;

    @JsonProperty("school_admission_score_year2")
    private BigDecimal schoolAdmissionScoreYear2;

    @JsonProperty("school_admission_score_year3")
    private BigDecimal schoolAdmissionScoreYear3;

    @JsonProperty("predicted_score")
    private BigDecimal predictedScore;

    @JsonProperty("enrollment_quota")
    private Integer enrollmentQuota;

    @JsonProperty("allocation_quota")
    private Integer allocationQuota;

    @JsonProperty("admission_rate")
    private BigDecimal admissionRate;

    @JsonProperty("rank_competition_ratio")
    private BigDecimal rankCompetitionRatio;

    @JsonProperty("probability")
    private Integer probability;

    @JsonProperty("confidence_interval")
    private BigDecimal confidenceInterval;

    @JsonProperty("lower_bound")
    private BigDecimal lowerBound;

    @JsonProperty("upper_bound")
    private BigDecimal upperBound;

    @JsonProperty("allocation_advantage")
    private Boolean allocationAdvantage;

    @JsonProperty("allocation_adjusted_probability")
    private Integer allocationAdjustedProbability;

    @JsonProperty("message")
    private String message;

    @JsonProperty("school_name")
    private String schoolName;

    @JsonProperty("school_rank")
    private Integer schoolRank;

    @JsonProperty("data_source")
    private String dataSource;
}
