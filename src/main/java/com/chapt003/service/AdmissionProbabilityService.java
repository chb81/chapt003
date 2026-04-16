package com.chapt003.service;

import com.chapt003.dto.AdmissionProbabilityDetailResponse;
import com.chapt003.entity.School;
import com.chapt003.entity.StudentScore;
import com.chapt003.repository.SchoolRepository;
import com.chapt003.repository.StudentScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class AdmissionProbabilityService {

    private static final BigDecimal WEIGHT_SCORE = new BigDecimal("0.5");
    private static final BigDecimal WEIGHT_ADMISSION_RATE = new BigDecimal("0.3");
    private static final BigDecimal WEIGHT_TREND = new BigDecimal("0.2");
    private static final BigDecimal HIGH_PROBABILITY = new BigDecimal("80.00");
    private static final BigDecimal MEDIUM_PROBABILITY = new BigDecimal("50.00");

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    public AdmissionProbabilityDetailResponse calculateProbability(Long userId, Long schoolId) {
        Optional<StudentScore> scoreOpt = studentScoreRepository.findByUserId(userId);
        if (!scoreOpt.isPresent()) {
            return AdmissionProbabilityDetailResponse.builder()
                    .probability(null)
                    .message("请先录入学生成绩")
                    .build();
        }

        StudentScore studentScore = scoreOpt.get();
        if (studentScore.getTotalScore() == null) {
            return AdmissionProbabilityDetailResponse.builder()
                    .probability(null)
                    .message("请完善学生成绩信息")
                    .build();
        }

        Optional<School> schoolOpt = schoolRepository.findById(schoolId);
        if (!schoolOpt.isPresent()) {
            return AdmissionProbabilityDetailResponse.builder()
                    .probability(null)
                    .message("学校信息不存在")
                    .build();
        }

        School school = schoolOpt.get();

        if (!hasSufficientAdmissionData(school)) {
            return AdmissionProbabilityDetailResponse.builder()
                    .probability(null)
                    .message("数据不足")
                    .build();
        }

        BigDecimal studentTotalScore = studentScore.getTotalScore();
        BigDecimal probabilityDecimal = calculateAdmissionProbability(studentTotalScore, school);
        String message = getProbabilityMessage(probabilityDecimal);
        Integer probability = probabilityDecimal != null ? probabilityDecimal.intValue() : null;

        return AdmissionProbabilityDetailResponse.builder()
                .studentTotalScore(studentTotalScore)
                .schoolAdmissionScoreYear1(school.getAdmissionScoreYear1())
                .schoolAdmissionScoreYear2(school.getAdmissionScoreYear2())
                .schoolAdmissionScoreYear3(school.getAdmissionScoreYear3())
                .admissionRate(calculateAdmissionRate(school))
                .probability(probability)
                .message(message)
                .build();
    }

    public BigDecimal calculateAdmissionProbability(BigDecimal studentScore, School school) {
        BigDecimal scoreMatch = calculateScoreMatch(studentScore, school);
        BigDecimal admissionRate = calculateAdmissionRate(school);
        BigDecimal trend = calculateTrend(school);

        BigDecimal probability = WEIGHT_SCORE.multiply(scoreMatch)
                .add(WEIGHT_ADMISSION_RATE.multiply(admissionRate))
                .add(WEIGHT_TREND.multiply(trend));

        probability = probability.multiply(new BigDecimal("100"));
        probability = probability.setScale(2, RoundingMode.HALF_UP);

        if (probability.compareTo(BigDecimal.ZERO) < 0) {
            probability = BigDecimal.ZERO;
        } else if (probability.compareTo(new BigDecimal("100")) > 0) {
            probability = new BigDecimal("100");
        }

        return probability;
    }

    private BigDecimal calculateScoreMatch(BigDecimal studentScore, School school) {
        BigDecimal minScore = getMinAdmissionScore(school);
        BigDecimal maxScore = getMaxAdmissionScore(school);

        if (minScore == null || maxScore == null) {
            return new BigDecimal("0.5");
        }

        if (minScore.compareTo(maxScore) == 0) {
            if (studentScore.compareTo(minScore) >= 0) {
                return BigDecimal.ONE;
            } else {
                return BigDecimal.ZERO;
            }
        }

        BigDecimal range = maxScore.subtract(minScore);
        BigDecimal diff = studentScore.subtract(minScore);
        BigDecimal scoreMatch = diff.divide(range, 4, RoundingMode.HALF_UP);

        return scoreMatch;
    }

    private BigDecimal calculateAdmissionRate(School school) {
        if (school.getEnrollmentQuota() == null || school.getApplicantCount() == null) {
            return new BigDecimal("0.5");
        }

        if (school.getApplicantCount() == 0) {
            return new BigDecimal("0.5");
        }

        BigDecimal rate = new BigDecimal(school.getEnrollmentQuota())
                .divide(new BigDecimal(school.getApplicantCount()), 4, RoundingMode.HALF_UP);

        return rate;
    }

    private BigDecimal calculateTrend(School school) {
        BigDecimal year1 = school.getAdmissionScoreYear1();
        BigDecimal year2 = school.getAdmissionScoreYear2();
        BigDecimal year3 = school.getAdmissionScoreYear3();

        if (year1 == null || year2 == null || year3 == null) {
            return new BigDecimal("0.5");
        }

        BigDecimal change12 = year1.subtract(year2);
        BigDecimal change23 = year2.subtract(year3);
        BigDecimal totalChange = change12.add(change23);

        if (totalChange.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal("0.5");
        }

        BigDecimal avgChange = totalChange.divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);
        BigDecimal avgScore = year1.add(year2).add(year3).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        BigDecimal changeRatio = avgChange.divide(avgScore, 4, RoundingMode.HALF_UP);

        BigDecimal trend = new BigDecimal("0.5").subtract(changeRatio);

        if (trend.compareTo(BigDecimal.ZERO) < 0) {
            trend = BigDecimal.ZERO;
        } else if (trend.compareTo(BigDecimal.ONE) > 0) {
            trend = BigDecimal.ONE;
        }

        return trend;
    }

    private BigDecimal getMinAdmissionScore(School school) {
        BigDecimal min = null;
        if (school.getAdmissionScoreYear1() != null) {
            min = school.getAdmissionScoreYear1();
        }
        if (school.getAdmissionScoreYear2() != null) {
            if (min == null || school.getAdmissionScoreYear2().compareTo(min) < 0) {
                min = school.getAdmissionScoreYear2();
            }
        }
        if (school.getAdmissionScoreYear3() != null) {
            if (min == null || school.getAdmissionScoreYear3().compareTo(min) < 0) {
                min = school.getAdmissionScoreYear3();
            }
        }
        return min;
    }

    private BigDecimal getMaxAdmissionScore(School school) {
        BigDecimal max = null;
        if (school.getAdmissionScoreYear1() != null) {
            max = school.getAdmissionScoreYear1();
        }
        if (school.getAdmissionScoreYear2() != null) {
            if (max == null || school.getAdmissionScoreYear2().compareTo(max) > 0) {
                max = school.getAdmissionScoreYear2();
            }
        }
        if (school.getAdmissionScoreYear3() != null) {
            if (max == null || school.getAdmissionScoreYear3().compareTo(max) > 0) {
                max = school.getAdmissionScoreYear3();
            }
        }
        return max;
    }

    private boolean hasSufficientAdmissionData(School school) {
        int dataCount = 0;
        if (school.getAdmissionScoreYear1() != null) dataCount++;
        if (school.getAdmissionScoreYear2() != null) dataCount++;
        if (school.getAdmissionScoreYear3() != null) dataCount++;
        return dataCount >= 2;
    }

    private String getProbabilityMessage(BigDecimal probability) {
        if (probability == null) {
            return "数据不足";
        }
        if (probability.compareTo(HIGH_PROBABILITY) >= 0) {
            return "录取概率较高";
        } else if (probability.compareTo(MEDIUM_PROBABILITY) >= 0) {
            return "录取概率中等";
        } else {
            return "录取概率较低";
        }
    }
}
