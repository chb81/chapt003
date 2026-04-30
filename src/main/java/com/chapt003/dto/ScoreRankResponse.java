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
public class ScoreRankResponse {

    private BigDecimal totalScore;
    private Integer cityRank;
    private String district;
    private Integer districtRank;
    private Integer totalStudents;
    private BigDecimal percentile;
    private Integer year;
    private String city;
    private String message;
}
