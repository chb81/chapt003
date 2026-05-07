package com.chapt003.service;

import com.chapt003.dto.SchoolRecommendationDTO;
import com.chapt003.dto.SmartRecommendationRequest;
import com.chapt003.dto.SmartRecommendationResponse;
import com.chapt003.entity.RecommendationPreference;
import com.chapt003.entity.TbSchool;
import com.chapt003.entity.StudentScore;
import com.chapt003.entity.VolunteerApplication;
import com.chapt003.entity.VolunteerApplicationItem;
import com.chapt003.repository.RecommendationPreferenceRepository;
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
public class SmartRecommendationService {

    private static final BigDecimal WEIGHT_PROBABILITY = new BigDecimal("0.5");
    private static final BigDecimal WEIGHT_PREFERENCE = new BigDecimal("0.25");
    private static final BigDecimal WEIGHT_LEVEL = new BigDecimal("0.15");
    private static final BigDecimal WEIGHT_TAG = new BigDecimal("0.1");

    @Autowired
    private AdmissionProbabilityService admissionProbabilityService;

    @Autowired
    private TbSchoolRepository tbSchoolRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private RecommendationPreferenceRepository recommendationPreferenceRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @Transactional(readOnly = true)
    public SmartRecommendationResponse generateRecommendations(Long userId, SmartRecommendationRequest request) {
        Optional<StudentScore> scoreOpt = studentScoreRepository.findByUserId(userId);
        if (!scoreOpt.isPresent()) {
            return SmartRecommendationResponse.builder()
                    .recommendations(Collections.emptyList())
                    .totalCount(0)
                    .message("请先录入学生成绩")
                    .build();
        }

        StudentScore studentScore = scoreOpt.get();
        if (studentScore.getTotalScore() == null) {
            return SmartRecommendationResponse.builder()
                    .recommendations(Collections.emptyList())
                    .totalCount(0)
                    .message("请完善学生成绩信息")
                    .build();
        }

        Optional<RecommendationPreference> preferenceOpt = recommendationPreferenceRepository
                .findActiveByUserId(userId);
        if (!preferenceOpt.isPresent()) {
            return SmartRecommendationResponse.builder()
                    .recommendations(Collections.emptyList())
                    .totalCount(0)
                    .message("请先设置推荐偏好")
                    .build();
        }

        RecommendationPreference preference = preferenceOpt.get();
        List<TbSchool> allSchools = tbSchoolRepository.findAll();

        List<SchoolRecommendationDTO> recommendations = allSchools.stream()
                .map(school -> calculateRecommendationScore(school, studentScore, preference))
                .filter(r -> r.getProbability() != null)
                .filter(r -> r.getProbability() >= preference.getMinProbability())
                .collect(Collectors.toList());

        recommendations = applyFilters(recommendations, request);
        applySorting(recommendations, request);

        if (recommendations.isEmpty()) {
            return SmartRecommendationResponse.builder()
                    .recommendations(Collections.emptyList())
                    .totalCount(0)
                    .message("未找到合适的推荐学校，建议调整地区偏好或考虑民办学校")
                    .build();
        }

        markAddedSchools(userId, recommendations);

        int maxResults = request.getMaxResults() != null ? request.getMaxResults() : preference.getMaxResults();
        if (!Boolean.TRUE.equals(request.getShowAll())) {
            recommendations = recommendations.stream()
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }

        return SmartRecommendationResponse.builder()
                .recommendations(recommendations)
                .totalCount(recommendations.size())
                .message(null)
                .build();
    }

    private SchoolRecommendationDTO calculateRecommendationScore(
            TbSchool school, StudentScore studentScore, RecommendationPreference preference) {

        BigDecimal probabilityDecimal = admissionProbabilityService
                .calculateAdmissionProbability(studentScore.getTotalScore(), school);
        Integer probability = probabilityDecimal != null ? probabilityDecimal.intValue() : null;

        if (probability == null) {
            return SchoolRecommendationDTO.builder()
                    .schoolId(school.getId())
                    .schoolName(school.getSchoolName())
                    .schoolType(school.getTypeName())
                    .probability(null)
                    .recommendationScore(BigDecimal.ZERO)
                    .reason("数据不足")
                    .allocationAdvantage(false)
                    .allocationTag(null)
                    .build();
        }

        BigDecimal preferenceScore = calculatePreferenceMatch(school, preference);
        BigDecimal levelScore = calculateLevelScore(school);

        BigDecimal recommendationScore = WEIGHT_PROBABILITY.multiply(new BigDecimal(probability).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .add(WEIGHT_PREFERENCE.multiply(preferenceScore))
                .add(WEIGHT_LEVEL.multiply(levelScore))
                .add(WEIGHT_TAG.multiply(BigDecimal.ZERO));

        recommendationScore = recommendationScore.setScale(4, RoundingMode.HALF_UP);

        String reason = generateRecommendationReason(school, preferenceScore, probability);

        return SchoolRecommendationDTO.builder()
                .schoolId(school.getId())
                .schoolName(school.getSchoolName())
                .schoolType(school.getTypeName())
                .probability(probability)
                .recommendationScore(recommendationScore)
                .reason(reason)
                .isAdded(false)
                .position(null)
                .allocationAdvantage(false)
                .allocationTag("统招竞争")
                .confidenceInterval(new BigDecimal("8"))
                .build();
    }

    private BigDecimal calculatePreferenceMatch(TbSchool school, RecommendationPreference preference) {
        BigDecimal districtMatch = calculateDistrictMatch(school, preference);
        BigDecimal typeMatch = calculateTypeMatch(school, preference);
        BigDecimal levelMatch = calculateLevelPreferenceMatch(school, preference);

        return districtMatch.multiply(new BigDecimal("0.4"))
                .add(typeMatch.multiply(new BigDecimal("0.3")))
                .add(levelMatch.multiply(new BigDecimal("0.3")))
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDistrictMatch(TbSchool school, RecommendationPreference preference) {
        if (preference.getPreferredDistricts() == null || preference.getPreferredDistricts().trim().isEmpty()) {
            return new BigDecimal("0.5");
        }
        String[] districts = preference.getPreferredDistricts().split(",");
        for (String district : districts) {
            if (school.getDistrict() != null && school.getDistrict().equals(district.trim())) {
                return BigDecimal.ONE;
            }
        }
        return new BigDecimal("0.2");
    }

    private BigDecimal calculateTypeMatch(TbSchool school, RecommendationPreference preference) {
        if (preference.getPreferredSchoolTypes() == null || preference.getPreferredSchoolTypes().trim().isEmpty()) {
            return new BigDecimal("0.5");
        }
        String[] types = preference.getPreferredSchoolTypes().split(",");
        for (String type : types) {
            if (school.getTypeName().equals(type.trim())) {
                return BigDecimal.ONE;
            }
        }
        return new BigDecimal("0.2");
    }

    private BigDecimal calculateLevelScore(TbSchool school) {
        String typeName = school.getTypeName();
        if ("KEY_HIGH_SCHOOL".equals(typeName)) return new BigDecimal("1.0");
        if ("REGULAR_HIGH_SCHOOL".equals(typeName)) return new BigDecimal("0.7");
        if ("VOCATIONAL_HIGH_SCHOOL".equals(typeName)) return new BigDecimal("0.4");
        return new BigDecimal("0.5");
    }

    private BigDecimal calculateLevelPreferenceMatch(TbSchool school, RecommendationPreference preference) {
        if (preference.getPreferredSchoolLevels() == null || preference.getPreferredSchoolLevels().trim().isEmpty()) {
            return new BigDecimal("0.5");
        }
        String[] levels = preference.getPreferredSchoolLevels().split(",");
        for (String level : levels) {
            if (school.getTypeName().equals(level.trim())) return BigDecimal.ONE;
        }
        return new BigDecimal("0.2");
    }

    private String generateRecommendationReason(TbSchool school, BigDecimal preferenceScore, Integer probability) {
        List<String> reasons = new ArrayList<>();
        if (probability >= 80) reasons.add("录取概率高");
        else if (probability >= 50) reasons.add("录取概率适中");
        if (preferenceScore.compareTo(new BigDecimal("0.7")) >= 0) reasons.add("符合您的偏好");
        if ("KEY_HIGH_SCHOOL".equals(school.getTypeName())) reasons.add("重点学校");
        return reasons.isEmpty() ? "综合推荐" : String.join("，", reasons);
    }

    private List<SchoolRecommendationDTO> applyFilters(List<SchoolRecommendationDTO> recommendations, SmartRecommendationRequest request) {
        if (request.getDistricts() != null && !request.getDistricts().isEmpty()) {
            recommendations = recommendations.stream()
                    .filter(r -> {
                        String district = getSchoolDistrict(r.getSchoolId());
                        return district != null && request.getDistricts().contains(district);
                    })
                    .collect(Collectors.toList());
        }
        if (request.getSchoolTypes() != null && !request.getSchoolTypes().isEmpty()) {
            recommendations = recommendations.stream()
                    .filter(r -> request.getSchoolTypes().contains(r.getSchoolType()))
                    .collect(Collectors.toList());
        }
        if (request.getMinScore() != null || request.getMaxScore() != null) {
            recommendations = recommendations.stream()
                    .filter(r -> {
                        if (request.getMinScore() != null && r.getProbability() < request.getMinScore()) return false;
                        if (request.getMaxScore() != null && r.getProbability() > request.getMaxScore()) return false;
                        return true;
                    })
                    .collect(Collectors.toList());
        }
        return recommendations;
    }

    private void applySorting(List<SchoolRecommendationDTO> recommendations, SmartRecommendationRequest request) {
        if (request.getSortBy() == null) return;
        switch (request.getSortBy()) {
            case "PROBABILITY_DESC":
                recommendations.sort((a, b) -> b.getProbability().compareTo(a.getProbability()));
                break;
            case "PROBABILITY_ASC":
                recommendations.sort((a, b) -> a.getProbability().compareTo(b.getProbability()));
                break;
            case "MATCH_SCORE_DESC":
                recommendations.sort((a, b) -> b.getRecommendationScore().compareTo(a.getRecommendationScore()));
                break;
            case "DISTRICT_ASC":
                recommendations.sort((a, b) -> {
                    String dA = getSchoolDistrict(a.getSchoolId());
                    String dB = getSchoolDistrict(b.getSchoolId());
                    if (dA == null) return dB != null ? -1 : 0;
                    if (dB == null) return 1;
                    return dA.compareTo(dB);
                });
                break;
        }
    }

    private String getSchoolDistrict(Long schoolId) {
        return tbSchoolRepository.findById(schoolId)
                .map(TbSchool::getDistrict).orElse(null);
    }

    private void markAddedSchools(Long userId, List<SchoolRecommendationDTO> recommendations) {
        List<VolunteerApplication> applications = volunteerApplicationRepository.findByUserId(userId);
        Map<Long, Integer> schoolPositionMap = new HashMap<>();
        for (VolunteerApplication app : applications) {
            for (VolunteerApplicationItem item : app.getItems()) {
                if (item.getSchool() != null) {
                    schoolPositionMap.put(item.getSchool().getId(), item.getPriority());
                }
            }
        }
        for (SchoolRecommendationDTO rec : recommendations) {
            if (schoolPositionMap.containsKey(rec.getSchoolId())) {
                rec.setIsAdded(true);
                rec.setPosition(schoolPositionMap.get(rec.getSchoolId()));
            }
        }
    }
}
