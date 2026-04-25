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
public class HistoricalAdmissionDataResponse {
    private Long id;
    private Long schoolId;
    private String schoolName;
    private Integer year;
    private BigDecimal admissionScore;
    private BigDecimal lowestScore;
    private BigDecimal highestScore;
    private BigDecimal averageScore;
    private Integer enrollmentQuota;
    private Integer applicantCount;
    private BigDecimal admissionRate;
    private String city;
    private String district;
    private String schoolType;
    private LocalDateTime createdAt;
}
