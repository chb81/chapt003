package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingStatusResponse {

    private Integer stepCompleted;
    private Boolean step1ScoreEntered;
    private Boolean step2RecommendationViewed;
    private Boolean step3PlanCreated;
    private Boolean onboardingCompleted;
    private String nextStep;
    private String nextStepDescription;
}
