package com.chapt003.service;

import com.chapt003.dto.AdmissionProbabilityDetailResponse;
import com.chapt003.entity.*;
import com.chapt003.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class AdmissionProbabilityService {

    private static final BigDecimal DEFAULT_W_RANK = new BigDecimal("0.40");
    private static final BigDecimal DEFAULT_W_SCORE = new BigDecimal("0.25");
    private static final BigDecimal DEFAULT_W_TREND = new BigDecimal("0.20");
    private static final BigDecimal DEFAULT_W_VOLATILITY = new BigDecimal("0.15");
    private static final String DEFAULT_CITY_CODE = "130000";
    private static final BigDecimal HIGH_PROBABILITY = new BigDecimal("80.00");
    private static final BigDecimal MEDIUM_PROBABILITY = new BigDecimal("50.00");

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private TbSchoolRepository tbSchoolRepository;

    @Autowired
    private TbScoreArchivesRepository tbScoreArchivesRepository;

    @Autowired
    private TbEnrollmentPlanRepository tbEnrollmentPlanRepository;

    @Autowired
    private TbAllocateStudentsRepository tbAllocateStudentsRepository;

    @Autowired
    private TbPredictionWeightRepository tbPredictionWeightRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    public AdmissionProbabilityDetailResponse calculateProbability(Long userId, Long schoolId) {
        Optional<StudentScore> scoreOpt = studentScoreRepository.findByUserId(userId);
        if (!scoreOpt.isPresent()) {
            return AdmissionProbabilityDetailResponse.builder()
                    .probability(null).message("请先录入学生成绩").build();
        }

        StudentScore studentScore = scoreOpt.get();
        if (studentScore.getTotalScore() == null) {
            return AdmissionProbabilityDetailResponse.builder()
                    .probability(null).message("请完善学生成绩信息").build();
        }

        Optional<TbSchool> schoolOpt = tbSchoolRepository.findById(schoolId);
        if (!schoolOpt.isPresent()) {
            return AdmissionProbabilityDetailResponse.builder()
                    .probability(null).message("学校信息不存在").build();
        }

        TbSchool school = schoolOpt.get();
        BigDecimal studentTotalScore = studentScore.getTotalScore();
        String latestYear = getLatestDataYear();

        TbEnrollmentPlan enrollmentPlan = null;
        if (school.getSchoolCode() != null && latestYear != null) {
            enrollmentPlan = tbEnrollmentPlanRepository
                    .findBySchoolCodeAndPlanYear(school.getSchoolCode(), latestYear)
                    .orElse(null);
        }

        Integer studentRank = findStudentRank(studentTotalScore, latestYear);

        BigDecimal probability;
        BigDecimal rankCompetitionRatio = null;
        BigDecimal predictedScore = null;
        Boolean allocationAdvantage = false;
        Integer allocationAdjustedProbability = null;
        Integer allocationQuota = null;
        BigDecimal admissionRate = null;
        String dataSource = "legacy";

        if (latestYear != null && studentRank != null) {
            dataSource = "enhanced";

            List<TbScoreArchives> archives = tbScoreArchivesRepository
                    .findByExayYearAndCityCodeOrderByScoreDesc(latestYear, DEFAULT_CITY_CODE);

            predictedScore = predictNextYearScore(school.getSchoolCode(), latestYear);

            int enrollmentQuota = enrollmentPlan != null && enrollmentPlan.getEnrollmentQuota() != null
                    ? enrollmentPlan.getEnrollmentQuota() : 0;
            int allocQuota = enrollmentPlan != null && enrollmentPlan.getAllocationQuota() != null
                    ? enrollmentPlan.getAllocationQuota() : 0;
            allocationQuota = allocQuota;

            if (enrollmentQuota > 0 && !archives.isEmpty()) {
                rankCompetitionRatio = new BigDecimal(enrollmentQuota)
                        .divide(new BigDecimal(studentRank), 4, RoundingMode.HALF_UP)
                        .min(BigDecimal.ONE);
                admissionRate = new BigDecimal(enrollmentQuota)
                        .divide(new BigDecimal(archives.get(0).getStudentsTotalNum()), 4, RoundingMode.HALF_UP);
            }

            StudentProfile profile = getStudentProfile(userId);
            String middleSchoolCode = null;
            if (profile != null && profile.getSchool() != null) {
                middleSchoolCode = findSchoolCodeByName(profile.getSchool());
            }

            if (middleSchoolCode != null && allocQuota > 0 && school.getSchoolCode() != null) {
                List<TbAllocateStudents> allocations = tbAllocateStudentsRepository
                        .findByMiddleAndHighSchoolAndYear(middleSchoolCode, school.getSchoolCode(), latestYear);
                if (!allocations.isEmpty()) {
                    allocationAdvantage = true;
                }
            }

            BigDecimal[] weights = loadWeights(latestYear);

            BigDecimal rankScore = calculateRankScore(studentRank, enrollmentQuota, archives);
            BigDecimal scoreMatch = calculateScoreMatchEnhanced(studentTotalScore, predictedScore, school);
            BigDecimal trend = calculateTrend(school);
            BigDecimal volatility = calculateVolatilityFactor(school);

            probability = weights[0].multiply(rankScore)
                    .add(weights[1].multiply(scoreMatch))
                    .add(weights[2].multiply(trend))
                    .add(weights[3].multiply(volatility))
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);

            probability = probability.max(BigDecimal.ZERO).min(new BigDecimal("100"));

            if (allocationAdvantage && enrollmentQuota > 0 && allocQuota > 0) {
                BigDecimal allocProb = calculateRankScore(studentRank, enrollmentQuota, archives);
                BigDecimal allocProbability = weights[0].multiply(allocProb)
                        .add(weights[1].multiply(scoreMatch))
                        .add(weights[2].multiply(trend))
                        .add(weights[3].multiply(volatility))
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
                allocProbability = allocProbability.max(BigDecimal.ZERO).min(new BigDecimal("100"));
                allocationAdjustedProbability = allocProbability.intValue();
            }

        } else {
            if (!hasSufficientAdmissionData(school)) {
                return AdmissionProbabilityDetailResponse.builder()
                        .probability(null).message("数据不足").build();
            }
            probability = calculateLegacyProbability(studentTotalScore, school);
            admissionRate = calculateLegacyAdmissionRate(school);
        }

        Integer probInt = probability != null ? probability.intValue() : null;
        BigDecimal confidence = calculateConfidenceInterval(school);
        BigDecimal lowerBound = probability != null
                ? probability.subtract(confidence).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP) : null;
        BigDecimal upperBound = probability != null
                ? probability.add(confidence).min(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) : null;

        return AdmissionProbabilityDetailResponse.builder()
                .studentTotalScore(studentTotalScore)
                .studentRank(studentRank)
                .schoolAdmissionScoreYear1(school.getAdmissionScoreYear1())
                .schoolAdmissionScoreYear2(school.getAdmissionScoreYear2())
                .schoolAdmissionScoreYear3(school.getAdmissionScoreYear3())
                .predictedScore(predictedScore)
                .enrollmentQuota(enrollmentPlan != null ? enrollmentPlan.getEnrollmentQuota() : school.getEnrollmentQuota())
                .allocationQuota(allocationQuota)
                .admissionRate(admissionRate)
                .rankCompetitionRatio(rankCompetitionRatio)
                .probability(probInt)
                .confidenceInterval(confidence)
                .lowerBound(lowerBound)
                .upperBound(upperBound)
                .allocationAdvantage(allocationAdvantage)
                .allocationAdjustedProbability(allocationAdjustedProbability)
                .message(getProbabilityMessage(probability))
                .schoolName(school.getSchoolName())
                .schoolRank(school.getSchoolRank())
                .dataSource(dataSource)
                .build();
    }

    public BigDecimal calculateAdmissionProbability(BigDecimal studentScore, TbSchool school) {
        String latestYear = getLatestDataYear();

        if (latestYear != null) {
            Integer rank = findStudentRank(studentScore, latestYear);
            if (rank != null && school.getSchoolCode() != null) {
                TbEnrollmentPlan plan = tbEnrollmentPlanRepository
                        .findBySchoolCodeAndPlanYear(school.getSchoolCode(), latestYear).orElse(null);
                int quota = plan != null && plan.getEnrollmentQuota() != null ? plan.getEnrollmentQuota() : 0;
                List<TbScoreArchives> archives = tbScoreArchivesRepository
                        .findByExayYearAndCityCodeOrderByScoreDesc(latestYear, DEFAULT_CITY_CODE);
                BigDecimal[] weights = loadWeights(latestYear);
                BigDecimal rankScore = calculateRankScore(rank, quota, archives);
                BigDecimal scoreMatch = calculateScoreMatchEnhanced(studentScore, null, school);
                BigDecimal trend = calculateTrend(school);
                BigDecimal volatility = calculateVolatilityFactor(school);
                BigDecimal prob = weights[0].multiply(rankScore)
                        .add(weights[1].multiply(scoreMatch))
                        .add(weights[2].multiply(trend))
                        .add(weights[3].multiply(volatility))
                        .multiply(new BigDecimal("100"));
                return prob.max(BigDecimal.ZERO).min(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return calculateLegacyProbability(studentScore, school);
    }

    private BigDecimal calculateRankScore(Integer studentRank, int enrollmentQuota, List<TbScoreArchives> archives) {
        if (enrollmentQuota <= 0 || archives.isEmpty()) {
            return new BigDecimal("0.5");
        }
        if (studentRank <= enrollmentQuota) {
            return BigDecimal.ONE;
        }
        BigDecimal ratio = new BigDecimal(enrollmentQuota)
                .divide(new BigDecimal(studentRank), 4, RoundingMode.HALF_UP);
        return ratio.min(BigDecimal.ONE);
    }

    private BigDecimal calculateScoreMatchEnhanced(BigDecimal studentScore, BigDecimal predictedScore, TbSchool school) {
        BigDecimal referenceScore = predictedScore;
        if (referenceScore == null) {
            referenceScore = school.getAdmissionScoreYear1();
        }
        if (referenceScore == null) {
            return new BigDecimal("0.5");
        }
        BigDecimal diff = studentScore.subtract(referenceScore);
        if (diff.compareTo(new BigDecimal("20")) >= 0) return BigDecimal.ONE;
        if (diff.compareTo(new BigDecimal("-20")) <= 0) return BigDecimal.ZERO;
        return new BigDecimal("0.5").add(diff.divide(new BigDecimal("40"), 4, RoundingMode.HALF_UP));
    }

    private BigDecimal predictNextYearScore(String schoolCode, String currentYear) {
        try {
            int year = Integer.parseInt(currentYear);
            BigDecimal y1 = getSchoolAdmissionScore(schoolCode, year);
            BigDecimal y2 = getSchoolAdmissionScore(schoolCode, year - 1);
            BigDecimal y3 = getSchoolAdmissionScore(schoolCode, year - 2);

            if (y1 != null && y2 != null && y3 != null) {
                BigDecimal trend = y1.subtract(y2).add(y2.subtract(y3)).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                return y1.add(trend).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private BigDecimal getSchoolAdmissionScore(String schoolCode, int year) {
        return null;
    }

    private String getLatestDataYear() {
        List<String> years = tbScoreArchivesRepository.findAvailableYears(DEFAULT_CITY_CODE);
        return years.isEmpty() ? null : years.get(0);
    }

    private Integer findStudentRank(BigDecimal totalScore, String year) {
        if (year == null) return null;
        Optional<TbScoreArchives> opt = tbScoreArchivesRepository
                .findByScoreAndExayYearAndCityCode(totalScore.intValue(), year, DEFAULT_CITY_CODE);
        return opt.map(TbScoreArchives::getRanking).orElse(null);
    }

    private String findSchoolCodeByName(String name) {
        if (name == null) return null;
        List<TbSchool> schools = tbSchoolRepository.findBySchoolNameContaining(name);
        if (!schools.isEmpty()) return schools.get(0).getSchoolCode();
        List<TbSchool> allMiddle = tbSchoolRepository.findBySchoolType("初中");
        for (TbSchool s : allMiddle) {
            if (name.contains(s.getSchoolName()) || s.getSchoolName().contains(name)) {
                return s.getSchoolCode();
            }
        }
        return null;
    }

    private StudentProfile getStudentProfile(Long userId) {
        try {
            return studentProfileRepository.findByUserId(userId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal[] loadWeights(String year) {
        BigDecimal w1 = DEFAULT_W_RANK, w2 = DEFAULT_W_SCORE, w3 = DEFAULT_W_TREND, w4 = DEFAULT_W_VOLATILITY;
        try {
            List<TbPredictionWeight> weights = tbPredictionWeightRepository.findByYearAndCityCode(year, DEFAULT_CITY_CODE);
            for (TbPredictionWeight w : weights) {
                switch (w.getWeightName()) {
                    case "rank_competition": w1 = w.getWeightValue(); break;
                    case "score_match": w2 = w.getWeightValue(); break;
                    case "trend": w3 = w.getWeightValue(); break;
                    case "volatility": w4 = w.getWeightValue(); break;
                }
            }
        } catch (Exception ignored) {}
        return new BigDecimal[]{w1, w2, w3, w4};
    }

    private BigDecimal calculateLegacyProbability(BigDecimal studentScore, TbSchool school) {
        BigDecimal scoreMatch = calculateScoreMatch(studentScore, school);
        BigDecimal admissionRate = calculateLegacyAdmissionRate(school);
        BigDecimal trend = calculateTrend(school);
        BigDecimal volatility = calculateVolatilityFactor(school);

        BigDecimal probability = DEFAULT_W_SCORE.multiply(scoreMatch)
                .add(DEFAULT_W_RANK.multiply(admissionRate))
                .add(DEFAULT_W_TREND.multiply(trend))
                .add(DEFAULT_W_VOLATILITY.multiply(volatility))
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        return probability.max(BigDecimal.ZERO).min(new BigDecimal("100"));
    }

    private BigDecimal calculateScoreMatch(BigDecimal studentScore, TbSchool school) {
        BigDecimal minScore = getMinAdmissionScore(school);
        BigDecimal maxScore = getMaxAdmissionScore(school);
        if (minScore == null || maxScore == null) return new BigDecimal("0.5");
        if (minScore.compareTo(maxScore) == 0) return studentScore.compareTo(minScore) >= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
        BigDecimal range = maxScore.subtract(minScore);
        BigDecimal diff = studentScore.subtract(minScore);
        return diff.divide(range, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLegacyAdmissionRate(TbSchool school) {
        if (school.getEnrollmentQuota() == null || school.getApplicantCount() == null || school.getApplicantCount() == 0)
            return new BigDecimal("0.5");
        return new BigDecimal(school.getEnrollmentQuota())
                .divide(new BigDecimal(school.getApplicantCount()), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTrend(TbSchool school) {
        BigDecimal y1 = school.getAdmissionScoreYear1();
        BigDecimal y2 = school.getAdmissionScoreYear2();
        BigDecimal y3 = school.getAdmissionScoreYear3();
        if (y1 == null || y2 == null || y3 == null) return new BigDecimal("0.5");
        BigDecimal totalChange = y1.subtract(y2).add(y2.subtract(y3));
        if (totalChange.compareTo(BigDecimal.ZERO) == 0) return new BigDecimal("0.5");
        BigDecimal avgChange = totalChange.divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);
        BigDecimal avgScore = y1.add(y2).add(y3).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        BigDecimal changeRatio = avgChange.divide(avgScore, 4, RoundingMode.HALF_UP);
        return new BigDecimal("0.5").subtract(changeRatio).max(BigDecimal.ZERO).min(BigDecimal.ONE);
    }

    private BigDecimal calculateVolatilityFactor(TbSchool school) {
        BigDecimal y1 = school.getAdmissionScoreYear1();
        BigDecimal y2 = school.getAdmissionScoreYear2();
        BigDecimal y3 = school.getAdmissionScoreYear3();
        if (y1 == null || y2 == null || y3 == null) return new BigDecimal("0.5");
        BigDecimal avg = y1.add(y2).add(y3).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        BigDecimal variance = y1.subtract(avg).pow(2).add(y2.subtract(avg).pow(2)).add(y3.subtract(avg).pow(2))
                .divide(new BigDecimal("3"), 4, RoundingMode.HALF_UP);
        BigDecimal stdDev = new BigDecimal(Math.sqrt(variance.doubleValue()));
        if (stdDev.compareTo(new BigDecimal("5")) <= 0) return new BigDecimal("0.9");
        if (stdDev.compareTo(new BigDecimal("15")) <= 0) {
            return BigDecimal.ONE.subtract(stdDev.subtract(new BigDecimal("5"))
                    .divide(new BigDecimal("10"), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("0.5")));
        }
        return new BigDecimal("0.3");
    }

    private BigDecimal getMinAdmissionScore(TbSchool school) {
        BigDecimal min = school.getAdmissionScoreYear1();
        if (school.getAdmissionScoreYear2() != null && (min == null || school.getAdmissionScoreYear2().compareTo(min) < 0))
            min = school.getAdmissionScoreYear2();
        if (school.getAdmissionScoreYear3() != null && (min == null || school.getAdmissionScoreYear3().compareTo(min) < 0))
            min = school.getAdmissionScoreYear3();
        return min;
    }

    private BigDecimal getMaxAdmissionScore(TbSchool school) {
        BigDecimal max = school.getAdmissionScoreYear1();
        if (school.getAdmissionScoreYear2() != null && (max == null || school.getAdmissionScoreYear2().compareTo(max) > 0))
            max = school.getAdmissionScoreYear2();
        if (school.getAdmissionScoreYear3() != null && (max == null || school.getAdmissionScoreYear3().compareTo(max) > 0))
            max = school.getAdmissionScoreYear3();
        return max;
    }

    private boolean hasSufficientAdmissionData(TbSchool school) {
        int c = 0;
        if (school.getAdmissionScoreYear1() != null) c++;
        if (school.getAdmissionScoreYear2() != null) c++;
        if (school.getAdmissionScoreYear3() != null) c++;
        return c >= 2;
    }

    private String getProbabilityMessage(BigDecimal probability) {
        if (probability == null) return "数据不足";
        if (probability.compareTo(HIGH_PROBABILITY) >= 0) return "录取概率较高";
        if (probability.compareTo(MEDIUM_PROBABILITY) >= 0) return "录取概率中等";
        return "录取概率较低";
    }

    private BigDecimal calculateConfidenceInterval(TbSchool school) {
        int dp = 0;
        if (school.getAdmissionScoreYear1() != null) dp++;
        if (school.getAdmissionScoreYear2() != null) dp++;
        if (school.getAdmissionScoreYear3() != null) dp++;
        if (dp < 2) return new BigDecimal("15");
        if (dp == 2) return new BigDecimal("10");
        BigDecimal y1 = school.getAdmissionScoreYear1();
        BigDecimal y2 = school.getAdmissionScoreYear2();
        BigDecimal y3 = school.getAdmissionScoreYear3();
        BigDecimal avg = y1.add(y2).add(y3).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        BigDecimal variance = y1.subtract(avg).pow(2).add(y2.subtract(avg).pow(2)).add(y3.subtract(avg).pow(2))
                .divide(new BigDecimal("3"), 4, RoundingMode.HALF_UP);
        BigDecimal stdDev = new BigDecimal(Math.sqrt(variance.doubleValue()));
        return stdDev.multiply(new BigDecimal("2")).min(new BigDecimal("15")).max(new BigDecimal("5"));
    }
}
