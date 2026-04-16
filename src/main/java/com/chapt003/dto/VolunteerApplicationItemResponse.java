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
public class VolunteerApplicationItemResponse {
    private Long id;
    private Long schoolId;
    private String schoolName;
    private String schoolType;
    private String city;
    private String district;
    private BigDecimal admissionScoreYear1;
    private BigDecimal admissionScoreYear2;
    private BigDecimal admissionScoreYear3;
    private Integer priority;
    private BigDecimal admissionProbability;
}
