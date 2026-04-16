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
public class SmartRecommendationRequest {

    @JsonProperty("min_probability")
    private Integer minProbability;

    @JsonProperty("max_results")
    private Integer maxResults;

    @JsonProperty("show_all")
    private Boolean showAll;

    @JsonProperty("districts")
    private List<String> districts;

    @JsonProperty("school_types")
    private List<String> schoolTypes;

    @JsonProperty("min_score")
    private Integer minScore;

    @JsonProperty("max_score")
    private Integer maxScore;

    @JsonProperty("sort_by")
    private String sortBy;
}
