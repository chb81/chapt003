package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRankMappingRequest {

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotNull(message = "年度不能为空")
    private Integer year;

    @NotNull(message = "总分不能为空")
    private BigDecimal totalScore;

    @NotNull(message = "全市排名不能为空")
    private Integer cityRank;

    private String district;

    private Integer districtRank;

    private Integer studentCount;

    private Integer cumulativeCount;
}
