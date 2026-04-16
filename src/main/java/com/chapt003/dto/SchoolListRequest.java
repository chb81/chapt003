package com.chapt003.dto;

import com.chapt003.entity.enums.SchoolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolListRequest {
    private String keyword;

    private String city;

    private String district;

    private SchoolType type;

    private BigDecimal minScore;

    private BigDecimal maxScore;

    private String sortBy;

    private String sortDirection;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;
}
