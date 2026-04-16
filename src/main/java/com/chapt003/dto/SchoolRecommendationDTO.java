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
public class SchoolRecommendationDTO {

    @JsonProperty("school_id")
    private Long schoolId;

    @JsonProperty("school_name")
    private String schoolName;

    @JsonProperty("school_type")
    private String schoolType;

    @JsonProperty("probability")
    private Integer probability;

    @JsonProperty("recommendation_score")
    private BigDecimal recommendationScore;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("is_added")
    private Boolean isAdded;

    @JsonProperty("position")
    private Integer position;
}
