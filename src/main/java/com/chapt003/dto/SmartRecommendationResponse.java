package com.chapt003.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartRecommendationResponse {

    @JsonProperty("recommendations")
    private List<SchoolRecommendationDTO> recommendations;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("message")
    private String message;
}
