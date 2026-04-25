package com.chapt003.service;

import com.chapt003.dto.HistoricalAdmissionDataRequest;
import com.chapt003.dto.HistoricalAdmissionDataResponse;
import com.chapt003.entity.HistoricalAdmissionData;
import com.chapt003.exception.BusinessException;
import com.chapt003.repository.HistoricalAdmissionDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricalAdmissionDataService {

    @Autowired
    private HistoricalAdmissionDataRepository repository;

    @Transactional(readOnly = true)
    public Page<HistoricalAdmissionDataResponse> getList(Integer year, String city, String district,
                                                         String schoolType, String keyword,
                                                         int page, int size) {
        Page<HistoricalAdmissionData> dataPage = repository.findWithFilters(
                year, city, district, schoolType, keyword,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "year")));
        return dataPage.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public List<HistoricalAdmissionDataResponse> getBySchoolId(Long schoolId) {
        return repository.findBySchoolIdOrderByYearDesc(schoolId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "historicalData", allEntries = true)
    public HistoricalAdmissionDataResponse create(HistoricalAdmissionDataRequest request) {
        if (repository.existsBySchoolIdAndYear(request.getSchoolId(), request.getYear())) {
            throw new BusinessException(400, "该学校 " + request.getYear() + " 年的数据已存在");
        }
        HistoricalAdmissionData data = convertToEntity(request);
        data = repository.save(data);
        return convertToResponse(data);
    }

    @Transactional
    @CacheEvict(value = "historicalData", allEntries = true)
    public HistoricalAdmissionDataResponse update(Long id, HistoricalAdmissionDataRequest request) {
        HistoricalAdmissionData data = repository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "记录不存在"));
        updateEntity(data, request);
        data = repository.save(data);
        return convertToResponse(data);
    }

    @Transactional
    @CacheEvict(value = "historicalData", allEntries = true)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(404, "记录不存在");
        }
        repository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = "historicalData", allEntries = true)
    public int batchImport(List<HistoricalAdmissionDataRequest> requests) {
        int count = 0;
        for (HistoricalAdmissionDataRequest request : requests) {
            HistoricalAdmissionData data = convertToEntity(request);
            repository.save(data);
            count++;
        }
        return count;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "historicalData", key = "'export'")
    public List<HistoricalAdmissionDataResponse> exportAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "year")).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private HistoricalAdmissionData convertToEntity(HistoricalAdmissionDataRequest request) {
        return HistoricalAdmissionData.builder()
                .schoolId(request.getSchoolId())
                .schoolName(request.getSchoolName())
                .year(request.getYear())
                .admissionScore(request.getAdmissionScore())
                .lowestScore(request.getLowestScore())
                .highestScore(request.getHighestScore())
                .averageScore(request.getAverageScore())
                .enrollmentQuota(request.getEnrollmentQuota())
                .applicantCount(request.getApplicantCount())
                .admissionRate(request.getAdmissionRate())
                .city(request.getCity())
                .district(request.getDistrict())
                .schoolType(request.getSchoolType())
                .build();
    }

    private void updateEntity(HistoricalAdmissionData data, HistoricalAdmissionDataRequest request) {
        if (request.getSchoolName() != null) data.setSchoolName(request.getSchoolName());
        if (request.getYear() != null) data.setYear(request.getYear());
        if (request.getAdmissionScore() != null) data.setAdmissionScore(request.getAdmissionScore());
        if (request.getLowestScore() != null) data.setLowestScore(request.getLowestScore());
        if (request.getHighestScore() != null) data.setHighestScore(request.getHighestScore());
        if (request.getAverageScore() != null) data.setAverageScore(request.getAverageScore());
        if (request.getEnrollmentQuota() != null) data.setEnrollmentQuota(request.getEnrollmentQuota());
        if (request.getApplicantCount() != null) data.setApplicantCount(request.getApplicantCount());
        if (request.getAdmissionRate() != null) data.setAdmissionRate(request.getAdmissionRate());
        if (request.getCity() != null) data.setCity(request.getCity());
        if (request.getDistrict() != null) data.setDistrict(request.getDistrict());
        if (request.getSchoolType() != null) data.setSchoolType(request.getSchoolType());
    }

    private HistoricalAdmissionDataResponse convertToResponse(HistoricalAdmissionData data) {
        return HistoricalAdmissionDataResponse.builder()
                .id(data.getId())
                .schoolId(data.getSchoolId())
                .schoolName(data.getSchoolName())
                .year(data.getYear())
                .admissionScore(data.getAdmissionScore())
                .lowestScore(data.getLowestScore())
                .highestScore(data.getHighestScore())
                .averageScore(data.getAverageScore())
                .enrollmentQuota(data.getEnrollmentQuota())
                .applicantCount(data.getApplicantCount())
                .admissionRate(data.getAdmissionRate())
                .city(data.getCity())
                .district(data.getDistrict())
                .schoolType(data.getSchoolType())
                .createdAt(data.getCreatedAt())
                .build();
    }
}
