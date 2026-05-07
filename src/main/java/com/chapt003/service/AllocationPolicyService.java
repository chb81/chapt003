package com.chapt003.service;

import com.chapt003.dto.AllocationQuotaRequest;
import com.chapt003.dto.AllocationQuotaResponse;
import com.chapt003.dto.AllocationPolicyRequest;
import com.chapt003.dto.AllocationPolicyResponse;
import com.chapt003.entity.AllocationPolicy;
import com.chapt003.entity.AllocationQuota;
import com.chapt003.entity.TbSchool;
import com.chapt003.entity.StudentProfile;
import com.chapt003.entity.StudentScore;
import com.chapt003.repository.AllocationPolicyRepository;
import com.chapt003.repository.AllocationQuotaRepository;
import com.chapt003.repository.TbSchoolRepository;
import com.chapt003.repository.StudentProfileRepository;
import com.chapt003.repository.StudentScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private TbSchoolRepository tbSchoolRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    public List<AllocationQuotaResponse> getQuotasBySchool(Long schoolId, Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<AllocationQuota> quotas = allocationQuotaRepository.findBySchoolIdAndYear(schoolId, year);
        String schoolName = tbSchoolRepository.findById(schoolId)
                .map(TbSchool::getSchoolName).orElse("");

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
            String schoolName = tbSchoolRepository.findById(q.getSchoolId())
                    .map(TbSchool::getSchoolName).orElse("");
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
                .eligibleConditions(p.getEligibleConditions())
                .description(p.getDescription())
                .isActive(p.getIsActive())
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
            String schoolName = tbSchoolRepository.findById(q.getSchoolId())
                    .map(TbSchool::getSchoolName).orElse("");
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

    @Transactional
    public AllocationQuotaResponse createQuota(AllocationQuotaRequest request) {
        AllocationQuota quota = AllocationQuota.builder()
                .schoolId(request.getSchoolId())
                .sourceSchoolName(request.getSourceSchoolName())
                .sourceSchoolCity(request.getSourceSchoolCity())
                .sourceSchoolDistrict(request.getSourceSchoolDistrict())
                .year(request.getYear())
                .quotaCount(request.getQuotaCount())
                .admissionScore(request.getAdmissionScore())
                .unifiedScore(request.getUnifiedScore())
                .scoreDifference(request.getScoreDifference())
                .policyRule(request.getPolicyRule())
                .build();
        quota = allocationQuotaRepository.save(quota);
        String schoolName = tbSchoolRepository.findById(request.getSchoolId())
                .map(TbSchool::getSchoolName).orElse("");
        return AllocationQuotaResponse.builder()
                .id(quota.getId())
                .schoolId(quota.getSchoolId())
                .schoolName(schoolName)
                .sourceSchoolName(quota.getSourceSchoolName())
                .sourceSchoolCity(quota.getSourceSchoolCity())
                .sourceSchoolDistrict(quota.getSourceSchoolDistrict())
                .year(quota.getYear())
                .quotaCount(quota.getQuotaCount())
                .admissionScore(quota.getAdmissionScore())
                .unifiedScore(quota.getUnifiedScore())
                .scoreDifference(quota.getScoreDifference())
                .policyRule(quota.getPolicyRule())
                .build();
    }

    @Transactional
    public AllocationQuotaResponse updateQuota(Long id, AllocationQuotaRequest request) {
        AllocationQuota quota = allocationQuotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分配生名额不存在: " + id));
        quota.setSchoolId(request.getSchoolId());
        quota.setSourceSchoolName(request.getSourceSchoolName());
        quota.setSourceSchoolCity(request.getSourceSchoolCity());
        quota.setSourceSchoolDistrict(request.getSourceSchoolDistrict());
        quota.setYear(request.getYear());
        quota.setQuotaCount(request.getQuotaCount());
        quota.setAdmissionScore(request.getAdmissionScore());
        quota.setUnifiedScore(request.getUnifiedScore());
        quota.setScoreDifference(request.getScoreDifference());
        quota.setPolicyRule(request.getPolicyRule());
        quota = allocationQuotaRepository.save(quota);
        String schoolName = tbSchoolRepository.findById(request.getSchoolId())
                .map(TbSchool::getSchoolName).orElse("");
        return AllocationQuotaResponse.builder()
                .id(quota.getId())
                .schoolId(quota.getSchoolId())
                .schoolName(schoolName)
                .sourceSchoolName(quota.getSourceSchoolName())
                .sourceSchoolCity(quota.getSourceSchoolCity())
                .sourceSchoolDistrict(quota.getSourceSchoolDistrict())
                .year(quota.getYear())
                .quotaCount(quota.getQuotaCount())
                .admissionScore(quota.getAdmissionScore())
                .unifiedScore(quota.getUnifiedScore())
                .scoreDifference(quota.getScoreDifference())
                .policyRule(quota.getPolicyRule())
                .build();
    }

    @Transactional
    public void deleteQuota(Long id) {
        AllocationQuota quota = allocationQuotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分配生名额不存在: " + id));
        allocationQuotaRepository.delete(quota);
    }

    public Page<AllocationQuotaResponse> getQuotaList(Integer year, Long schoolId, String sourceSchoolName, Pageable pageable) {
        Page<AllocationQuota> page;
        if (schoolId != null && year != null) {
            page = allocationQuotaRepository.findBySchoolIdAndYear(schoolId, year, pageable);
        } else if (year != null) {
            page = allocationQuotaRepository.findByYear(year, pageable);
        } else if (sourceSchoolName != null && !sourceSchoolName.isEmpty()) {
            page = allocationQuotaRepository.findBySourceSchoolNameContaining(sourceSchoolName, pageable);
        } else {
            page = allocationQuotaRepository.findAll(pageable);
        }
        return page.map(q -> {
            String schoolName = tbSchoolRepository.findById(q.getSchoolId())
                    .map(TbSchool::getSchoolName).orElse("");
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
        });
    }

    @Transactional
    public int batchImportQuotas(List<AllocationQuotaRequest> requests) {
        int count = 0;
        for (AllocationQuotaRequest req : requests) {
            AllocationQuota quota = AllocationQuota.builder()
                    .schoolId(req.getSchoolId())
                    .sourceSchoolName(req.getSourceSchoolName())
                    .sourceSchoolCity(req.getSourceSchoolCity())
                    .sourceSchoolDistrict(req.getSourceSchoolDistrict())
                    .year(req.getYear())
                    .quotaCount(req.getQuotaCount())
                    .admissionScore(req.getAdmissionScore())
                    .unifiedScore(req.getUnifiedScore())
                    .scoreDifference(req.getScoreDifference())
                    .policyRule(req.getPolicyRule())
                    .build();
            allocationQuotaRepository.save(quota);
            count++;
        }
        return count;
    }

    @Transactional
    public AllocationPolicyResponse createPolicy(AllocationPolicyRequest request) {
        AllocationPolicy policy = AllocationPolicy.builder()
                .city(request.getCity())
                .district(request.getDistrict())
                .year(request.getYear())
                .policyName(request.getPolicyName())
                .policyType(request.getPolicyType())
                .totalQuotaPercentage(request.getTotalQuotaPercentage())
                .minScoreGap(request.getMinScoreGap())
                .eligibleConditions(request.getEligibleConditions())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .build();
        policy = allocationPolicyRepository.save(policy);
        return AllocationPolicyResponse.builder()
                .id(policy.getId())
                .city(policy.getCity())
                .district(policy.getDistrict())
                .year(policy.getYear())
                .policyName(policy.getPolicyName())
                .policyType(policy.getPolicyType())
                .totalQuotaPercentage(policy.getTotalQuotaPercentage())
                .minScoreGap(policy.getMinScoreGap())
                .eligibleConditions(policy.getEligibleConditions())
                .description(policy.getDescription())
                .isActive(policy.getIsActive())
                .build();
    }

    @Transactional
    public AllocationPolicyResponse updatePolicy(Long id, AllocationPolicyRequest request) {
        AllocationPolicy policy = allocationPolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分配生政策不存在: " + id));
        policy.setCity(request.getCity());
        policy.setDistrict(request.getDistrict());
        policy.setYear(request.getYear());
        policy.setPolicyName(request.getPolicyName());
        policy.setPolicyType(request.getPolicyType());
        policy.setTotalQuotaPercentage(request.getTotalQuotaPercentage());
        policy.setMinScoreGap(request.getMinScoreGap());
        policy.setEligibleConditions(request.getEligibleConditions());
        policy.setDescription(request.getDescription());
        policy.setIsActive(request.getIsActive());
        policy = allocationPolicyRepository.save(policy);
        return AllocationPolicyResponse.builder()
                .id(policy.getId())
                .city(policy.getCity())
                .district(policy.getDistrict())
                .year(policy.getYear())
                .policyName(policy.getPolicyName())
                .policyType(policy.getPolicyType())
                .totalQuotaPercentage(policy.getTotalQuotaPercentage())
                .minScoreGap(policy.getMinScoreGap())
                .eligibleConditions(policy.getEligibleConditions())
                .description(policy.getDescription())
                .isActive(policy.getIsActive())
                .build();
    }

    @Transactional
    public void deletePolicy(Long id) {
        AllocationPolicy policy = allocationPolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分配生政策不存在: " + id));
        allocationPolicyRepository.delete(policy);
    }

    public Page<AllocationPolicyResponse> getPolicyList(Integer year, String city, String district, Pageable pageable) {
        Page<AllocationPolicy> page;
        if (city != null && district != null && year != null) {
            page = allocationPolicyRepository.findByCityAndDistrictAndYear(city, district, year, pageable);
        } else if (year != null) {
            page = allocationPolicyRepository.findByYear(year, pageable);
        } else if (city != null) {
            page = allocationPolicyRepository.findByCity(city, pageable);
        } else {
            page = allocationPolicyRepository.findAll(pageable);
        }
        return page.map(p -> AllocationPolicyResponse.builder()
                .id(p.getId())
                .city(p.getCity())
                .district(p.getDistrict())
                .year(p.getYear())
                .policyName(p.getPolicyName())
                .policyType(p.getPolicyType())
                .totalQuotaPercentage(p.getTotalQuotaPercentage())
                .minScoreGap(p.getMinScoreGap())
                .eligibleConditions(p.getEligibleConditions())
                .description(p.getDescription())
                .isActive(p.getIsActive())
                .build()
        );
    }
}
