package com.chapt003.service;

import com.chapt003.dto.SchoolListRequest;
import com.chapt003.dto.SchoolListResponse;
import com.chapt003.dto.SchoolRequest;
import com.chapt003.dto.SchoolResponse;
import com.chapt003.entity.TbSchool;
import com.chapt003.exception.BusinessException;
import com.chapt003.repository.TbSchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolService {

    @Autowired
    private TbSchoolRepository tbSchoolRepository;

    public SchoolListResponse getSchoolList(SchoolListRequest request) {
        Sort sort = buildSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<TbSchool> schoolPage = tbSchoolRepository.searchSchools(
                request.getKeyword(),
                request.getCity(),
                request.getDistrict(),
                request.getSchoolType(),
                request.getMinScore(),
                request.getMaxScore(),
                pageable
        );

        List<SchoolResponse> schools = schoolPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return SchoolListResponse.builder()
                .schools(schools)
                .totalElements(schoolPage.getTotalElements())
                .totalPages(schoolPage.getTotalPages())
                .currentPage(schoolPage.getNumber())
                .pageSize(schoolPage.getSize())
                .build();
    }

    @Cacheable(value = "schoolDetail", key = "#id")
    public SchoolResponse getSchoolById(Long id) {
        TbSchool school = tbSchoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "学校不存在"));
        return convertToResponse(school);
    }

    @Cacheable(value = "cities")
    public List<String> getAllCities() {
        return tbSchoolRepository.findAllCities();
    }

    @Cacheable(value = "districts", key = "#city")
    public List<String> getDistrictsByCity(String city) {
        return tbSchoolRepository.findDistrictsByCity(city);
    }

    @Transactional(readOnly = true)
    public List<SchoolResponse> exportAllSchools() {
        return tbSchoolRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"schools", "schoolDetail", "cities", "districts"}, allEntries = true)
    public SchoolResponse createSchool(SchoolRequest request) {
        if (tbSchoolRepository.existsBySchoolName(request.getName())) {
            throw new BusinessException(400, "学校名称已存在");
        }

        TbSchool school = TbSchool.builder()
                .schoolName(request.getName())
                .schoolCode(request.getSchoolCode())
                .schoolType(request.getSchoolType())
                .schoolNature(request.getSchoolNature())
                .city(request.getCity())
                .district(request.getDistrict())
                .areaCode(request.getAreaCode())
                .schoolRank(request.getSchoolRank())
                .admissionScoreYear1(request.getAdmissionScoreYear1())
                .admissionScoreYear2(request.getAdmissionScoreYear2())
                .admissionScoreYear3(request.getAdmissionScoreYear3())
                .description(request.getDescription())
                .features(request.getFeatures())
                .enrollmentQuota(request.getEnrollmentQuota())
                .applicantCount(request.getApplicantCount())
                .phone(request.getPhone())
                .address(request.getAddress())
                .schoolLevel(request.getSchoolLevel())
                .schoolRemark(request.getSchoolRemark())
                .deleted(false)
                .build();

        TbSchool savedSchool = tbSchoolRepository.save(school);
        return convertToResponse(savedSchool);
    }

    @Transactional
    @CacheEvict(value = {"schools", "schoolDetail", "cities", "districts"}, allEntries = true)
    public SchoolResponse updateSchool(Long id, SchoolRequest request) {
        TbSchool school = tbSchoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "学校不存在"));

        if (!school.getSchoolName().equals(request.getName()) && tbSchoolRepository.existsBySchoolName(request.getName())) {
            throw new BusinessException(400, "学校名称已存在");
        }

        school.setSchoolName(request.getName());
        school.setSchoolCode(request.getSchoolCode());
        school.setSchoolType(request.getSchoolType());
        school.setSchoolNature(request.getSchoolNature());
        school.setCity(request.getCity());
        school.setDistrict(request.getDistrict());
        school.setAreaCode(request.getAreaCode());
        school.setSchoolRank(request.getSchoolRank());
        school.setAdmissionScoreYear1(request.getAdmissionScoreYear1());
        school.setAdmissionScoreYear2(request.getAdmissionScoreYear2());
        school.setAdmissionScoreYear3(request.getAdmissionScoreYear3());
        school.setDescription(request.getDescription());
        school.setFeatures(request.getFeatures());
        school.setEnrollmentQuota(request.getEnrollmentQuota());
        school.setApplicantCount(request.getApplicantCount());
        school.setPhone(request.getPhone());
        school.setAddress(request.getAddress());
        school.setSchoolLevel(request.getSchoolLevel());
        school.setSchoolRemark(request.getSchoolRemark());

        TbSchool updatedSchool = tbSchoolRepository.save(school);
        return convertToResponse(updatedSchool);
    }

    @Transactional
    @CacheEvict(value = {"schools", "schoolDetail", "cities", "districts"}, allEntries = true)
    public void deleteSchool(Long id) {
        if (!tbSchoolRepository.existsById(id)) {
            throw new BusinessException(404, "学校不存在");
        }
        tbSchoolRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = {"schools", "schoolDetail", "cities", "districts"}, allEntries = true)
    public void importSchools(List<SchoolRequest> schoolRequests) {
        for (SchoolRequest request : schoolRequests) {
            if (!tbSchoolRepository.existsBySchoolName(request.getName())) {
                TbSchool school = TbSchool.builder()
                        .schoolName(request.getName())
                        .schoolCode(request.getSchoolCode())
                        .schoolType(request.getSchoolType())
                        .schoolNature(request.getSchoolNature())
                        .city(request.getCity())
                        .district(request.getDistrict())
                        .areaCode(request.getAreaCode())
                        .schoolRank(request.getSchoolRank())
                        .admissionScoreYear1(request.getAdmissionScoreYear1())
                        .admissionScoreYear2(request.getAdmissionScoreYear2())
                        .admissionScoreYear3(request.getAdmissionScoreYear3())
                        .description(request.getDescription())
                        .features(request.getFeatures())
                        .enrollmentQuota(request.getEnrollmentQuota())
                        .applicantCount(request.getApplicantCount())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .schoolLevel(request.getSchoolLevel())
                        .schoolRemark(request.getSchoolRemark())
                        .deleted(false)
                        .build();
                tbSchoolRepository.save(school);
            }
        }
    }

    private Sort buildSort(String sortBy, String sortDirection) {
        String sortField = sortBy != null ? sortBy : "schoolName";
        Sort.Direction direction = Sort.Direction.ASC;

        if ("desc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        if ("admissionScore".equalsIgnoreCase(sortField)) {
            sortField = "admissionScoreYear1";
        } else if ("name".equalsIgnoreCase(sortField)) {
            sortField = "schoolName";
        } else if ("schoolRank".equalsIgnoreCase(sortField)) {
            sortField = "schoolRank";
        }

        return Sort.by(direction, sortField);
    }

    private SchoolResponse convertToResponse(TbSchool school) {
        return SchoolResponse.builder()
                .id(school.getId())
                .schoolCode(school.getSchoolCode())
                .name(school.getSchoolName())
                .schoolType(school.getSchoolType())
                .schoolNature(school.getSchoolNature())
                .type(school.getTypeName())
                .city(school.getCity())
                .district(school.getDistrict())
                .areaCode(school.getAreaCode())
                .schoolRank(school.getSchoolRank())
                .admissionScoreYear1(school.getAdmissionScoreYear1())
                .admissionScoreYear2(school.getAdmissionScoreYear2())
                .admissionScoreYear3(school.getAdmissionScoreYear3())
                .description(school.getDescription())
                .features(school.getFeatures())
                .enrollmentQuota(school.getEnrollmentQuota())
                .applicantCount(school.getApplicantCount())
                .phone(school.getPhone())
                .address(school.getAddress())
                .schoolLevel(school.getSchoolLevel())
                .schoolRemark(school.getSchoolRemark())
                .createdAt(school.getCreatedAt())
                .updatedAt(school.getUpdatedAt())
                .build();
    }
}
