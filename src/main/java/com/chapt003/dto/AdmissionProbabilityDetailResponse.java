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

    @JsonProperty("school_admission_score_year1")
    private BigDecimal schoolAdmissionScoreYear1;

    @JsonProperty("school_admission_score_year2")
    private BigDecimal schoolAdmissionScoreYear2;

    @JsonProperty("school_admission_score_year3")
    private BigDecimal schoolAdmissionScoreYear3;

    @JsonProperty("admission_rate")
    private BigDecimal admissionRate;

    @JsonProperty("probability")
    private Integer probability;

    @JsonProperty("message")
    private String message;
}
