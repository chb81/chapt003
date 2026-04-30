package com.chapt003.service;

import com.chapt003.dto.AllocationQuotaResponse;
import com.chapt003.dto.AllocationPolicyResponse;
import com.chapt003.entity.AllocationPolicy;
import com.chapt003.entity.AllocationQuota;
import com.chapt003.entity.School;
import com.chapt003.entity.StudentProfile;
import com.chapt003.entity.StudentScore;
import com.chapt003.repository.AllocationPolicyRepository;
import com.chapt003.repository.AllocationQuotaRepository;
import com.chapt003.repository.SchoolRepository;
import com.chapt003.repository.StudentProfileRepository;
import com.chapt003.repository.StudentScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AllocationPolicyService {

    @Autowired
    private AllocationQuotaRepository allocationQuotaRepository;

    @Autowired
    private AllocationPolicyRepository allocationPolicyRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    public List<AllocationQuotaResponse> getQuotasBySchool(Long schoolId, Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<AllocationQuota> quotas = allocationQuotaRepository.findBySchoolIdAndYear(schoolId, year);
        String schoolName = schoolRepository.findById(schoolId)
                .map(School::getName).orElse("");

        return quotas.stream().map(q -> AllocationQuotaResponse.builder()
                .id(q.getId())
                .schoolId(q.getSchoolId())
                .schoolName(schoolName)
                .sourceSchoolName(q.getSourceSchoolName())
                .sourceSchoolCity(q.getSourceSchoolCity())
                .sourceSchoolDistrict(q.getSourceSchoolDistrict())
                .year(q.getYear())
                .quotaCount(q.getQuotaCount())
                .admissionScore(q.getAdmissionScore())
                .unifiedScore(q.getUnifiedScore())
                .scoreDifference(q.getScoreDifference())
                .policyRule(q.getPolicyRule())
                .build()
        ).collect(Collectors.toList());
    }

    public List<AllocationQuotaResponse> getQuotasBySourceSchool(String sourceSchoolName, Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<AllocationQuota> quotas = allocationQuotaRepository.findBySourceSchoolNameAndYear(sourceSchoolName, year);
        return quotas.stream().map(q -> {
            String schoolName = schoolRepository.findById(q.getSchoolId())
                    .map(School::getName).orElse("");
            return AllocationQuotaResponse.builder()
                    .id(q.getId())
                    .schoolId(q.getSchoolId())
                    .schoolName(schoolName)
                    .sourceSchoolName(q.getSourceSchoolName())
                    .sourceSchoolCity(q.getSourceSchoolCity())
                    .sourceSchoolDistrict(q.getSourceSchoolDistrict())
                    .year(q.getYear())
                    .quotaCount(q.getQuotaCount())
                    .admissionScore(q.getAdmissionScore())
                    .unifiedScore(q.getUnifiedScore())
                    .scoreDifference(q.getScoreDifference())
                    .policyRule(q.getPolicyRule())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<AllocationPolicyResponse> getPoliciesByCityDistrict(String city, String district, Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<AllocationPolicy> policies = allocationPolicyRepository.findByCityAndDistrictAndYear(city, district, year);
        return policies.stream().map(p -> AllocationPolicyResponse.builder()
                .id(p.getId())
                .city(p.getCity())
                .district(p.getDistrict())
                .year(p.getYear())
                .policyName(p.getPolicyName())
                .policyType(p.getPolicyType())
                .totalQuotaPercentage(p.getTotalQuotaPercentage())
                .minScoreGap(p.getMinScoreGap())
                .description(p.getDescription())
                .build()
        ).collect(Collectors.toList());
    }

    public BigDecimal calculateAllocationAdjustedProbability(Long userId, Long schoolId) {
        Optional<StudentScore> scoreOpt = studentScoreRepository.findByUserId(userId);
        if (!scoreOpt.isPresent() || scoreOpt.get().getTotalScore() == null) {
            return null;
        }

        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUserId(userId);
        if (!profileOpt.isPresent()) {
            return null;
        }

        StudentProfile profile = profileOpt.get();
        StudentScore score = scoreOpt.get();
        String sourceSchool = profile.getSchool();
        if (sourceSchool == null || sourceSchool.isEmpty()) {
            return null;
        }

        Integer currentYear = Year.now().getValue();
        Optional<AllocationQuota> quotaOpt = allocationQuotaRepository
                .findBySchoolAndSourceAndYear(schoolId, sourceSchool, currentYear);

        if (!quotaOpt.isPresent()) {
            return null;
        }

        AllocationQuota quota = quotaOpt.isPresent() ? quotaOpt.get() : null;
        if (quota == null || quota.getAdmissionScore() == null || quota.getUnifiedScore() == null) {
            return null;
        }

        BigDecimal studentScore = score.getTotalScore();
        BigDecimal allocationLine = quota.getAdmissionScore();
        BigDecimal unifiedLine = quota.getUnifiedScore();

        if (studentScore.compareTo(unifiedLine) >= 0) {
            return new BigDecimal("100");
        }

        if (studentScore.compareTo(allocationLine) >= 0) {
            BigDecimal range = unifiedLine.subtract(allocationLine);
            if (range.compareTo(BigDecimal.ZERO) == 0) {
                return new BigDecimal("100");
            }
            BigDecimal position = studentScore.subtract(allocationLine);
            return position.divide(range, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal gap = allocationLine.subtract(studentScore);
        BigDecimal range = unifiedLine.subtract(allocationLine);
        if (range.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal ratio = gap.divide(range, 4, RoundingMode.HALF_UP);
        BigDecimal probability = new BigDecimal("40").multiply(BigDecimal.ONE.subtract(ratio));
        return probability.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean hasAllocationAdvantage(Long userId, Long schoolId) {
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUserId(userId);
        if (!profileOpt.isPresent() || profileOpt.get().getSchool() == null) {
            return false;
        }
        Integer currentYear = Year.now().getValue();
        return allocationQuotaRepository
                .findBySchoolAndSourceAndYear(schoolId, profileOpt.get().getSchool(), currentYear)
                .isPresent();
    }

    public List<AllocationQuotaResponse> getStudentAllocationOptions(Long userId) {
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUserId(userId);
        if (!profileOpt.isPresent() || profileOpt.get().getSchool() == null) {
            return Collections.emptyList();
        }
        Integer currentYear = Year.now().getValue();
        List<AllocationQuota> quotas = allocationQuotaRepository
                .findBySourceSchoolNameAndYear(profileOpt.get().getSchool(), currentYear);
        return quotas.stream().map(q -> {
            String schoolName = schoolRepository.findById(q.getSchoolId())
                    .map(School::getName).orElse("");
            boolean advantage = q.getScoreDifference() != null && q.getScoreDifference().compareTo(BigDecimal.ZERO) > 0;
            return AllocationQuotaResponse.builder()
                    .id(q.getId())
                    .schoolId(q.getSchoolId())
                    .schoolName(schoolName)
                    .sourceSchoolName(q.getSourceSchoolName())
                    .year(q.getYear())
                    .quotaCount(q.getQuotaCount())
                    .admissionScore(q.getAdmissionScore())
                    .unifiedScore(q.getUnifiedScore())
                    .scoreDifference(q.getScoreDifference())
                    .hasAllocationAdvantage(advantage)
                    .advantageDescription(advantage ? "分配生可降" + q.getScoreDifference() + "分录取" : "无分配生优惠")
                    .build();
        }).collect(Collectors.toList());
    }
}
