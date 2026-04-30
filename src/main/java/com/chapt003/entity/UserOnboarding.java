package com.chapt003.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_onboarding")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOnboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "step_completed", nullable = false)
    @Builder.Default
    private Integer stepCompleted = 0;

    @Column(name = "step1_score_entered", nullable = false)
    @Builder.Default
    private Boolean step1ScoreEntered = false;

    @Column(name = "step2_recommendation_viewed", nullable = false)
    @Builder.Default
    private Boolean step2RecommendationViewed = false;

    @Column(name = "step3_plan_created", nullable = false)
    @Builder.Default
    private Boolean step3PlanCreated = false;

    @Column(name = "onboarding_completed", nullable = false)
    @Builder.Default
    private Boolean onboardingCompleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getStepCompleted() { return stepCompleted; }
    public void setStepCompleted(Integer stepCompleted) { this.stepCompleted = stepCompleted; }
    public Boolean getStep1ScoreEntered() { return step1ScoreEntered; }
    public void setStep1ScoreEntered(Boolean step1ScoreEntered) { this.step1ScoreEntered = step1ScoreEntered; }
    public Boolean getStep2RecommendationViewed() { return step2RecommendationViewed; }
    public void setStep2RecommendationViewed(Boolean step2RecommendationViewed) { this.step2RecommendationViewed = step2RecommendationViewed; }
    public Boolean getStep3PlanCreated() { return step3PlanCreated; }
    public void setStep3PlanCreated(Boolean step3PlanCreated) { this.step3PlanCreated = step3PlanCreated; }
    public Boolean getOnboardingCompleted() { return onboardingCompleted; }
    public void setOnboardingCompleted(Boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }
}
