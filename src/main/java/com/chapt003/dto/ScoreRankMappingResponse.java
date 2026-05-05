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
public class ScoreRankMappingResponse {

    private Long id;
    private String city;
    private Integer year;
    private BigDecimal totalScore;
    private Integer cityRank;
    private String district;
    private Integer districtRank;
    private Integer studentCount;
    private Integer cumulativeCount;
}
