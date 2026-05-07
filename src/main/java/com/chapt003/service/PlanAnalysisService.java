package com.chapt003.service;

import com.chapt003.dto.PlanComparisonResponse;
import com.chapt003.dto.RiskAssessmentResponse;
import com.chapt003.entity.TbSchool;
import com.chapt003.entity.StudentScore;
import com.chapt003.entity.VolunteerApplication;
import com.chapt003.entity.VolunteerApplicationItem;
import com.chapt003.repository.TbSchoolRepository;
import com.chapt003.repository.StudentScoreRepository;
import com.chapt003.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanAnalysisService {

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @Autowired
    private AdmissionProbabilityService admissionProbabilityService;

    @Autowired
    private AllocationPolicyService allocationPolicyService;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private TbSchoolRepository tbSchoolRepository;

    @Transactional(readOnly = true)
    public RiskAssessmentResponse assessRisk(Long userId, Long planId) {
        Optional<VolunteerApplication> planOpt = volunteerApplicationRepository.findByIdWithItems(planId);
        if (!planOpt.isPresent()) {
            return RiskAssessmentResponse.builder()
                    .planId(planId)
                    .overallRiskLevel("UNKNOWN")
                    .summary("方案不存在")
                    .build();
        }

        VolunteerApplication plan = planOpt.get();
        List<VolunteerApplicationItem> items = plan.getItems();
        if (items == null || items.isEmpty()) {
            return RiskAssessmentResponse.builder()
                    .planId(planId)
                    .planName(plan.getSimulationName())
                    .overallRiskLevel("HIGH")
                    .summary("方案中没有学校")
                    .suggestions(Collections.singletonList("请添加学校到志愿方案"))
                    .build();
        }

        List<RiskAssessmentResponse.SchoolRiskDetail> schoolRisks = new ArrayList<>();
        BigDecimal totalProbability = BigDecimal.ZERO;
        BigDecimal maxProb = BigDecimal.ZERO;
        BigDecimal minProb = new BigDecimal("100");

        for (VolunteerApplicationItem item : items) {
            TbSchool school = item.getSchool();
            Long schoolId = school != null ? school.getId() : null;
            BigDecimal probability;
            try {
                probability = admissionProbabilityService.calculateAdmissionProbability(
                        getStudentScore(userId), school);
            } catch (Exception e) {
                probability = BigDecimal.ZERO;
            }
            if (probability == null) probability = BigDecimal.ZERO;

            totalProbability = totalProbability.add(probability);
            if (probability.compareTo(maxProb) > 0) maxProb = probability;
            if (probability.compareTo(minProb) < 0) minProb = probability;

            BigDecimal confidence = calculateConfidenceInterval(schoolId);
            BigDecimal lower = probability.subtract(confidence).max(BigDecimal.ZERO);
            BigDecimal upper = probability.add(confidence).min(new BigDecimal("100"));

            Integer order = item.getPriority() != null ? item.getPriority() : 0;
            String level = getProbabilityLevel(probability);
            String tag = getRiskTag(order, probability, items.size());

            String schoolName = school != null ? school.getName() : "未知学校";
            schoolRisks.add(RiskAssessmentResponse.SchoolRiskDetail.builder()
                    .schoolId(schoolId)
                    .schoolName(schoolName)
                    .order(order)
                    .probability(probability)
                    .probabilityLevel(level)
                    .riskTag(tag)
                    .confidenceInterval(confidence)
                    .lowerBound(lower)
                    .upperBound(upper)
                    .build());
        }

        schoolRisks.sort(Comparator.comparing(RiskAssessmentResponse.SchoolRiskDetail::getOrder));

        BigDecimal slideProb = calculateSlideProbability(schoolRisks);
        BigDecimal safetyScore = calculateSafetyScore(schoolRisks);
        BigDecimal sprintScore = calculateSprintScore(schoolRisks);
        BigDecimal balanceScore = calculateBalanceScore(schoolRisks);
        BigDecimal overallRiskScore = slideProb.multiply(new BigDecimal("100"))
                .add(BigDecimal.ONE.subtract(safetyScore).multiply(new BigDecimal("30")))
                .add(BigDecimal.ONE.subtract(balanceScore).multiply(new BigDecimal("20")));

        String riskLevel = determineRiskLevel(slideProb, safetyScore, minProb);
        List<String> suggestions = generateSuggestions(schoolRisks, slideProb, safetyScore, sprintScore);
        String summary = generateSummary(riskLevel, slideProb, safetyScore, schoolRisks.size());

        return RiskAssessmentResponse.builder()
                .planId(planId)
                .planName(plan.getSimulationName())
                .overallRiskLevel(riskLevel)
                .overallRiskScore(overallRiskScore.setScale(2, RoundingMode.HALF_UP))
                .slideProbability(slideProb.setScale(2, RoundingMode.HALF_UP))
                .safetyScore(safetyScore.setScale(2, RoundingMode.HALF_UP))
                .sprintScore(sprintScore.setScale(2, RoundingMode.HALF_UP))
                .balanceScore(balanceScore.setScale(2, RoundingMode.HALF_UP))
                .schoolRisks(schoolRisks)
                .suggestions(suggestions)
                .summary(summary)
                .build();
    }

    @Transactional(readOnly = true)
    public PlanComparisonResponse comparePlans(Long userId, List<Long> planIds) {
        if (planIds == null || planIds.size() < 2) {
            return PlanComparisonResponse.builder()
                    .recommendation("请至少选择两个方案进行对比")
                    .build();
        }

        List<PlanComparisonResponse.PlanSummary> summaries = new ArrayList<>();
        for (Long planId : planIds) {
            RiskAssessmentResponse risk = assessRisk(userId, planId);
            summaries.add(PlanComparisonResponse.PlanSummary.builder()
                    .planId(planId)
                    .planName(risk.getPlanName())
                    .schoolCount(risk.getSchoolRisks() != null ? risk.getSchoolRisks().size() : 0)
                    .averageProbability(calculateAvgProb(risk))
                    .totalProbability(calculateTotalProb(risk))
                    .maxProbability(calculateMaxProb(risk))
                    .minProbability(calculateMinProb(risk))
                    .riskLevel(risk.getOverallRiskLevel())
                    .slideProbability(risk.getSlideProbability())
                    .sprintScore(risk.getSprintScore())
                    .build());
        }

        PlanComparisonResponse.ComparisonMetrics metrics = buildComparisonMetrics(summaries);
        String recommendation = generateComparisonRecommendation(summaries, metrics);

        return PlanComparisonResponse.builder()
                .plans(summaries)
                .metrics(metrics)
                .recommendation(recommendation)
                .recommendationReason(metrics.getSaferPlan() + "方案整体风险更低，建议优先考虑")
                .build();
    }

    private BigDecimal calculateSlideProbability(List<RiskAssessmentResponse.SchoolRiskDetail> risks) {
        if (risks.isEmpty()) return BigDecimal.ONE;

        BigDecimal allFailProb = BigDecimal.ONE;
        for (RiskAssessmentResponse.SchoolRiskDetail risk : risks) {
            BigDecimal failProb = BigDecimal.ONE.subtract(
                    risk.getProbability().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            allFailProb = allFailProb.multiply(failProb);
        }
        return allFailProb.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSafetyScore(List<RiskAssessmentResponse.SchoolRiskDetail> risks) {
        List<RiskAssessmentResponse.SchoolRiskDetail> reversed = new ArrayList<>(risks);
        reversed.sort(Comparator.comparing(RiskAssessmentResponse.SchoolRiskDetail::getOrder).reversed());
        for (RiskAssessmentResponse.SchoolRiskDetail risk : reversed) {
            if (risk.getProbability().compareTo(new BigDecimal("70")) >= 0) {
                return risk.getProbability().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            }
        }
        if (!reversed.isEmpty()) {
            return reversed.get(reversed.size() - 1).getProbability()
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateSprintScore(List<RiskAssessmentResponse.SchoolRiskDetail> risks) {
        return risks.stream()
                .filter(r -> r.getProbability().compareTo(new BigDecimal("30")) >= 0
                        && r.getProbability().compareTo(new BigDecimal("60")) <= 0)
                .map(r -> r.getProbability().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateBalanceScore(List<RiskAssessmentResponse.SchoolRiskDetail> risks) {
        if (risks.size() < 2) return BigDecimal.ZERO;
        long levels = risks.stream()
                .map(RiskAssessmentResponse.SchoolRiskDetail::getProbabilityLevel)
                .distinct()
                .count();
        return new BigDecimal(Math.min(levels, 3))
                .divide(new BigDecimal("3"), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateConfidenceInterval(Long schoolId) {
        return new BigDecimal("8");
    }

    private String getProbabilityLevel(BigDecimal probability) {
        if (probability.compareTo(new BigDecimal("70")) >= 0) return "HIGH";
        if (probability.compareTo(new BigDecimal("40")) >= 0) return "MEDIUM";
        return "LOW";
    }

    private String getRiskTag(Integer order, BigDecimal probability, int total) {
        if (order <= Math.ceil(total / 3.0)) {
            return probability.compareTo(new BigDecimal("40")) >= 0 ? "冲刺" : "高风险冲刺";
        } else if (order <= Math.ceil(total * 2.0 / 3.0)) {
            return "稳妥";
        } else {
            return probability.compareTo(new BigDecimal("70")) >= 0 ? "安全保底" : "保底偏弱";
        }
    }

    private String determineRiskLevel(BigDecimal slideProb, BigDecimal safetyScore, BigDecimal minProb) {
        if (slideProb.compareTo(new BigDecimal("30")) > 0) return "HIGH";
        if (slideProb.compareTo(new BigDecimal("10")) > 0 || safetyScore.compareTo(new BigDecimal("0.7")) < 0) return "MEDIUM";
        return "LOW";
    }

    private List<String> generateSuggestions(List<RiskAssessmentResponse.SchoolRiskDetail> risks,
                                              BigDecimal slideProb, BigDecimal safetyScore, BigDecimal sprintScore) {
        List<String> suggestions = new ArrayList<>();

        if (slideProb.compareTo(new BigDecimal("20")) > 0) {
            suggestions.add("滑档概率较高（" + slideProb + "%），建议增加保底学校");
        }
        if (safetyScore.compareTo(new BigDecimal("0.6")) < 0) {
            suggestions.add("保底校录取概率偏低，建议替换为更稳妥的学校");
        }

        Optional<RiskAssessmentResponse.SchoolRiskDetail> lastSafe = risks.stream()
                .filter(r -> r.getProbability().compareTo(new BigDecimal("90")) > 0)
                .findFirst();
        if (!lastSafe.isPresent()) {
            suggestions.add("方案中没有高概率学校，建议增加至少一所保底校（录取概率大于90%）");
        }

        boolean hasHighRiskInFront = risks.stream()
                .filter(r -> r.getOrder() <= 2)
                .anyMatch(r -> r.getProbability().compareTo(new BigDecimal("20")) < 0);
        if (hasHighRiskInFront) {
            suggestions.add("前两个志愿中有录取概率很低的学校，建议调整为冲刺校或移到后面");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("方案整体配置合理，风险可控");
        }

        return suggestions;
    }

    private String generateSummary(String riskLevel, BigDecimal slideProb, BigDecimal safetyScore, int schoolCount) {
        String riskText = "HIGH".equals(riskLevel) ? "高风险" : ("MEDIUM".equals(riskLevel) ? "中等风险" : "低风险");
        return String.format("方案包含%d所学校，整体风险等级为%s，滑档概率%.1f%%，保底安全度%.0f%%",
                schoolCount, riskText, slideProb, safetyScore.multiply(new BigDecimal("100")));
    }

    private BigDecimal getStudentScore(Long userId) {
        return studentScoreRepository.findByUserId(userId)
                .filter(s -> s.getTotalScore() != null)
                .map(s -> s.getTotalScore())
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateAvgProb(RiskAssessmentResponse risk) {
        if (risk.getSchoolRisks() == null || risk.getSchoolRisks().isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = risk.getSchoolRisks().stream()
                .map(RiskAssessmentResponse.SchoolRiskDetail::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(new BigDecimal(risk.getSchoolRisks().size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalProb(RiskAssessmentResponse risk) {
        if (risk.getSchoolRisks() == null) return BigDecimal.ZERO;
        return risk.getSchoolRisks().stream()
                .map(RiskAssessmentResponse.SchoolRiskDetail::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMaxProb(RiskAssessmentResponse risk) {
        if (risk.getSchoolRisks() == null || risk.getSchoolRisks().isEmpty()) return BigDecimal.ZERO;
        return risk.getSchoolRisks().stream()
                .map(RiskAssessmentResponse.SchoolRiskDetail::getProbability)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMinProb(RiskAssessmentResponse risk) {
        if (risk.getSchoolRisks() == null || risk.getSchoolRisks().isEmpty()) return BigDecimal.ZERO;
        return risk.getSchoolRisks().stream()
                .map(RiskAssessmentResponse.SchoolRiskDetail::getProbability)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private PlanComparisonResponse.ComparisonMetrics buildComparisonMetrics(List<PlanComparisonResponse.PlanSummary> summaries) {
        if (summaries.size() < 2) return PlanComparisonResponse.ComparisonMetrics.builder().build();

        PlanComparisonResponse.PlanSummary p1 = summaries.get(0);
        PlanComparisonResponse.PlanSummary p2 = summaries.get(1);

        BigDecimal probDiff = p1.getAverageProbability().subtract(p2.getAverageProbability()).abs();
        BigDecimal riskDiff = p1.getSlideProbability().subtract(p2.getSlideProbability()).abs();

        String safer = p1.getSlideProbability().compareTo(p2.getSlideProbability()) <= 0
                ? p1.getPlanName() : p2.getPlanName();
        String aggressive = p1.getSprintScore() != null && p2.getSprintScore() != null
                && p1.getSprintScore().compareTo(p2.getSprintScore()) > 0
                ? p1.getPlanName() : p2.getPlanName();

        return PlanComparisonResponse.ComparisonMetrics.builder()
                .probabilityDifference(probDiff)
                .riskDifference(riskDiff)
                .saferPlan(safer)
                .moreAggressivePlan(aggressive)
                .build();
    }

    private String generateComparisonRecommendation(List<PlanComparisonResponse.PlanSummary> summaries,
                                                      PlanComparisonResponse.ComparisonMetrics metrics) {
        if (metrics == null || metrics.getSaferPlan() == null) return "无法生成建议";
        return "推荐" + metrics.getSaferPlan() + "方案";
    }
}
