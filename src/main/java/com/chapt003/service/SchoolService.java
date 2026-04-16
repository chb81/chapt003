package com.chapt003.service;

import com.chapt003.dto.SchoolListRequest;
import com.chapt003.dto.SchoolListResponse;
import com.chapt003.dto.SchoolRequest;
import com.chapt003.dto.SchoolResponse;
import com.chapt003.entity.School;
import com.chapt003.entity.enums.SchoolType;
import com.chapt003.exception.BusinessException;
import com.chapt003.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SchoolRepository schoolRepository;

    public SchoolListResponse getSchoolList(SchoolListRequest request) {
        Sort sort = buildSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<School> schoolPage = schoolRepository.searchSchools(
                request.getKeyword(),
                request.getCity(),
                request.getDistrict(),
                request.getType(),
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

    public SchoolResponse getSchoolById(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "学校不存在"));
        return convertToResponse(school);
    }

    public List<String> getAllCities() {
        return schoolRepository.findAllCities();
    }

    public List<String> getDistrictsByCity(String city) {
        return schoolRepository.findDistrictsByCity(city);
    }

    @Transactional
    public SchoolResponse createSchool(SchoolRequest request) {
        if (schoolRepository.existsByName(request.getName())) {
            throw new BusinessException(400, "学校名称已存在");
        }

        School school = School.builder()
                .name(request.getName())
                .type(request.getType())
                .city(request.getCity())
                .district(request.getDistrict())
                .admissionScoreYear1(request.getAdmissionScoreYear1())
                .admissionScoreYear2(request.getAdmissionScoreYear2())
                .admissionScoreYear3(request.getAdmissionScoreYear3())
                .description(request.getDescription())
                .features(request.getFeatures())
                .enrollmentQuota(request.getEnrollmentQuota())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        School savedSchool = schoolRepository.save(school);
        return convertToResponse(savedSchool);
    }

    @Transactional
    public SchoolResponse updateSchool(Long id, SchoolRequest request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "学校不存在"));

        if (!school.getName().equals(request.getName()) && schoolRepository.existsByName(request.getName())) {
            throw new BusinessException(400, "学校名称已存在");
        }

        school.setName(request.getName());
        school.setType(request.getType());
        school.setCity(request.getCity());
        school.setDistrict(request.getDistrict());
        school.setAdmissionScoreYear1(request.getAdmissionScoreYear1());
        school.setAdmissionScoreYear2(request.getAdmissionScoreYear2());
        school.setAdmissionScoreYear3(request.getAdmissionScoreYear3());
        school.setDescription(request.getDescription());
        school.setFeatures(request.getFeatures());
        school.setEnrollmentQuota(request.getEnrollmentQuota());
        school.setPhone(request.getPhone());
        school.setAddress(request.getAddress());

        School updatedSchool = schoolRepository.save(school);
        return convertToResponse(updatedSchool);
    }

    @Transactional
    public void deleteSchool(Long id) {
        if (!schoolRepository.existsById(id)) {
            throw new BusinessException(404, "学校不存在");
        }
        schoolRepository.deleteById(id);
    }

    @Transactional
    public void importSchools(List<SchoolRequest> schoolRequests) {
        for (SchoolRequest request : schoolRequests) {
            if (!schoolRepository.existsByName(request.getName())) {
                School school = School.builder()
                        .name(request.getName())
                        .type(request.getType())
                        .city(request.getCity())
                        .district(request.getDistrict())
                        .admissionScoreYear1(request.getAdmissionScoreYear1())
                        .admissionScoreYear2(request.getAdmissionScoreYear2())
                        .admissionScoreYear3(request.getAdmissionScoreYear3())
                        .description(request.getDescription())
                        .features(request.getFeatures())
                        .enrollmentQuota(request.getEnrollmentQuota())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .build();
                schoolRepository.save(school);
            }
        }
    }

    private Sort buildSort(String sortBy, String sortDirection) {
        String sortField = sortBy != null ? sortBy : "name";
        Sort.Direction direction = Sort.Direction.ASC;

        if ("desc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        if ("admissionScore".equalsIgnoreCase(sortField)) {
            sortField = "admissionScoreYear1";
        }

        return Sort.by(direction, sortField);
    }

    private SchoolResponse convertToResponse(School school) {
        return SchoolResponse.builder()
                .id(school.getId())
                .name(school.getName())
                .type(school.getType())
                .city(school.getCity())
                .district(school.getDistrict())
                .admissionScoreYear1(school.getAdmissionScoreYear1())
                .admissionScoreYear2(school.getAdmissionScoreYear2())
                .admissionScoreYear3(school.getAdmissionScoreYear3())
                .description(school.getDescription())
                .features(school.getFeatures())
                .enrollmentQuota(school.getEnrollmentQuota())
                .phone(school.getPhone())
                .address(school.getAddress())
                .createdAt(school.getCreatedAt())
                .updatedAt(school.getUpdatedAt())
                .build();
    }
}
