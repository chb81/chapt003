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
public class StudentScoreResponse {
    private Long id;
    private Long userId;
    private BigDecimal chinese;
    private BigDecimal math;
    private BigDecimal english;
    private BigDecimal physics;
    private BigDecimal chemistry;
    private BigDecimal politics;
    private BigDecimal history;
    private BigDecimal geography;
    private BigDecimal biology;
    private BigDecimal totalScore;
    private BigDecimal averageScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
