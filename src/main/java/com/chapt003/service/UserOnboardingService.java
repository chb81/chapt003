package com.chapt003.service;

import com.chapt003.dto.OnboardingStatusResponse;
import com.chapt003.entity.UserOnboarding;
import com.chapt003.repository.StudentScoreRepository;
import com.chapt003.repository.UserOnboardingRepository;
import com.chapt003.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOnboardingService {

    @Autowired
    private UserOnboardingRepository onboardingRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    public OnboardingStatusResponse getOnboardingStatus(Long userId) {
        UserOnboarding onboarding = onboardingRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserOnboarding newOnboarding = UserOnboarding.builder()
                            .userId(userId)
                            .stepCompleted(0)
                            .step1ScoreEntered(false)
                            .step2RecommendationViewed(false)
                            .step3PlanCreated(false)
                            .onboardingCompleted(false)
                            .build();
                    return onboardingRepository.save(newOnboarding);
                });

        autoDetectProgress(userId, onboarding);

        String nextStep = determineNextStep(onboarding);

        return OnboardingStatusResponse.builder()
                .stepCompleted(onboarding.getStepCompleted())
                .step1ScoreEntered(onboarding.getStep1ScoreEntered())
                .step2RecommendationViewed(onboarding.getStep2RecommendationViewed())
                .step3PlanCreated(onboarding.getStep3PlanCreated())
                .onboardingCompleted(onboarding.getOnboardingCompleted())
                .nextStep(nextStep)
                .nextStepDescription(getStepDescription(nextStep))
                .build();
    }

    public OnboardingStatusResponse completeStep(Long userId, Integer step) {
        UserOnboarding onboarding = onboardingRepository.findByUserId(userId)
                .orElseGet(() -> UserOnboarding.builder().userId(userId).build());

        switch (step) {
            case 1:
                onboarding.setStep1ScoreEntered(true);
                break;
            case 2:
                onboarding.setStep2RecommendationViewed(true);
                break;
            case 3:
                onboarding.setStep3PlanCreated(true);
                break;
            default:
                break;
        }

        int completed = 0;
        if (onboarding.getStep1ScoreEntered()) completed++;
        if (onboarding.getStep2RecommendationViewed()) completed++;
        if (onboarding.getStep3PlanCreated()) completed++;
        onboarding.setStepCompleted(completed);

        if (completed >= 3) {
            onboarding.setOnboardingCompleted(true);
        }

        onboardingRepository.save(onboarding);

        return getOnboardingStatus(userId);
    }

    private void autoDetectProgress(Long userId, UserOnboarding onboarding) {
        boolean changed = false;

        if (!onboarding.getStep1ScoreEntered()) {
            boolean hasScore = studentScoreRepository.findByUserId(userId).isPresent();
            if (hasScore) {
                onboarding.setStep1ScoreEntered(true);
                changed = true;
            }
        }

        if (!onboarding.getStep3PlanCreated()) {
            long planCount = volunteerApplicationRepository.countByUserId(userId);
            if (planCount > 0) {
                onboarding.setStep3PlanCreated(true);
                changed = true;
            }
        }

        if (changed) {
            int completed = 0;
            if (onboarding.getStep1ScoreEntered()) completed++;
            if (onboarding.getStep2RecommendationViewed()) completed++;
            if (onboarding.getStep3PlanCreated()) completed++;
            onboarding.setStepCompleted(completed);
            if (completed >= 3) onboarding.setOnboardingCompleted(true);
            onboardingRepository.save(onboarding);
        }
    }

    private String determineNextStep(UserOnboarding onboarding) {
        if (!onboarding.getStep1ScoreEntered()) return "ENTER_SCORE";
        if (!onboarding.getStep2RecommendationViewed()) return "VIEW_RECOMMENDATION";
        if (!onboarding.getStep3PlanCreated()) return "CREATE_PLAN";
        return "COMPLETED";
    }

    private String getStepDescription(String step) {
        if (step == null) return null;
        switch (step) {
            case "ENTER_SCORE": return "录入您孩子的中考成绩";
            case "VIEW_RECOMMENDATION": return "查看智能推荐学校";
            case "CREATE_PLAN": return "创建您的第一个志愿方案";
            case "COMPLETED": return "引导已完成";
            default: return null;
        }
    }
}
