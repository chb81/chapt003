package com.chapt003.service;

import com.chapt003.dto.*;
import com.chapt003.entity.School;
import com.chapt003.entity.User;
import com.chapt003.entity.VolunteerApplication;
import com.chapt003.entity.VolunteerApplicationItem;
import com.chapt003.entity.enums.VolunteerApplicationStatus;
import com.chapt003.exception.BusinessException;
import com.chapt003.repository.SchoolRepository;
import com.chapt003.repository.StudentScoreRepository;
import com.chapt003.repository.UserRepository;
import com.chapt003.repository.VolunteerApplicationItemRepository;
import com.chapt003.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VolunteerApplicationService {

    private static final int MAX_VOLUNTEER_ITEMS = 8;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @Autowired
    private VolunteerApplicationItemRepository itemRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    @Transactional
    public VolunteerApplicationResponse createApplication(String email, VolunteerApplicationRequest request) {
        User user = getUserByEmail(email);
        Long userId = user.getId();

        if (request.getSimulationName() != null && !request.getSimulationName().trim().isEmpty()) {
            if (volunteerApplicationRepository.existsByUserIdAndYearAndStatus(userId, request.getYear(), VolunteerApplicationStatus.SIMULATION)) {
                throw new BusinessException(400, "该年份已存在模拟方案，请使用不同的名称");
            }
        } else {
            long draftCount = volunteerApplicationRepository.countDraftByUserIdAndYear(userId, request.getYear());
            if (draftCount > 0) {
                throw new BusinessException(400, "该年份已存在草稿，请编辑现有草稿或提交后再创建新的");
            }
        }

        VolunteerApplication application = VolunteerApplication.builder()
                .user(user)
                .year(request.getYear())
                .status(request.getSimulationName() != null && !request.getSimulationName().trim().isEmpty()
                        ? VolunteerApplicationStatus.SIMULATION
                        : VolunteerApplicationStatus.DRAFT)
                .simulationName(request.getSimulationName())
                .build();

        VolunteerApplication savedApplication = volunteerApplicationRepository.save(application);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (VolunteerApplicationItemRequest itemRequest : request.getItems()) {
                addItemToApplication(savedApplication, itemRequest);
            }
        }

        return convertToResponse(savedApplication);
    }

    public VolunteerApplicationResponse getApplicationById(String email, Long applicationId) {
        User user = getUserByEmail(email);
        VolunteerApplication application = volunteerApplicationRepository.findByIdAndUserId(applicationId, user.getId())
                .orElseThrow(() -> new BusinessException(404, "志愿填报申请不存在"));

        VolunteerApplication applicationWithItems = volunteerApplicationRepository.findByIdWithItems(applicationId)
                .orElse(application);

        return convertToResponse(applicationWithItems);
    }

    @Transactional(readOnly = true)
    public VolunteerApplicationListResponse getApplicationsByUser(String email, int page, int size) {
        User user = getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<VolunteerApplication> applicationPage = volunteerApplicationRepository
                .findByUserIdAndStatus(user.getId(), VolunteerApplicationStatus.DRAFT, pageable);

        List<VolunteerApplicationListResponse.VolunteerApplicationSummary> summaries = applicationPage.getContent().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());

        return VolunteerApplicationListResponse.builder()
                .applications(summaries)
                .totalElements(applicationPage.getTotalElements())
                .totalPages(applicationPage.getTotalPages())
                .currentPage(applicationPage.getNumber())
                .pageSize(applicationPage.getSize())
                .build();
    }

    @Transactional(readOnly = true)
    public VolunteerApplicationListResponse getSimulationsByUser(String email, int page, int size) {
        User user = getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<VolunteerApplication> applicationPage = volunteerApplicationRepository
                .findSimulationsByUserId(user.getId(), pageable);

        List<VolunteerApplicationListResponse.VolunteerApplicationSummary> summaries = applicationPage.getContent().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());

        return VolunteerApplicationListResponse.builder()
                .applications(summaries)
                .totalElements(applicationPage.getTotalElements())
                .totalPages(applicationPage.getTotalPages())
                .currentPage(applicationPage.getNumber())
                .pageSize(applicationPage.getSize())
                .build();
    }

    @Transactional(readOnly = true)
    public List<VolunteerApplicationResponse> getApplicationHistory(String email) {
        User user = getUserByEmail(email);
        List<VolunteerApplication> applications = volunteerApplicationRepository
                .findByUserIdAndStatus(user.getId(), VolunteerApplicationStatus.SUBMITTED);

        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VolunteerApplicationResponse addItem(String email, Long applicationId, VolunteerApplicationItemRequest request) {
        User user = getUserByEmail(email);
        VolunteerApplication application = volunteerApplicationRepository.findByIdAndUserId(applicationId, user.getId())
                .orElseThrow(() -> new BusinessException(404, "志愿填报申请不存在"));

        if (application.getStatus() == VolunteerApplicationStatus.SUBMITTED) {
            throw new BusinessException(400, "已提交的志愿不能修改");
        }

        long itemCount = itemRepository.countByVolunteerApplicationId(applicationId);
        if (itemCount >= MAX_VOLUNTEER_ITEMS) {
            throw new BusinessException(400, "志愿数量已达上限（最多8个）");
        }

        if (itemRepository.existsByVolunteerApplicationIdAndSchoolId(applicationId, request.getSchoolId())) {
            throw new BusinessException(400, "该学校已在志愿列表中");
        }

        if (itemRepository.findByVolunteerApplicationIdAndPriority(applicationId, request.getPriority()).isPresent()) {
            throw new BusinessException(400, "该优先级已被占用");
        }

        addItemToApplication(application, request);

        VolunteerApplication updatedApplication = volunteerApplicationRepository.findByIdWithItems(applicationId)
                .orElse(application);

        return convertToResponse(updatedApplication);
    }

    @Transactional
    public VolunteerApplicationResponse removeItem(String email, Long applicationId, Long schoolId) {
        User user = getUserByEmail(email);
        VolunteerApplication application = volunteerApplicationRepository.findByIdAndUserId(applicationId, user.getId())
                .orElseThrow(() -> new BusinessException(404, "志愿填报申请不存在"));

        if (application.getStatus() == VolunteerApplicationStatus.SUBMITTED) {
            throw new BusinessException(400, "已提交的志愿不能修改");
        }

        if (!itemRepository.existsByVolunteerApplicationIdAndSchoolId(applicationId, schoolId)) {
            throw new BusinessException(404, "该学校不在志愿列表中");
        }

        itemRepository.deleteByVolunteerApplicationIdAndSchoolId(applicationId, schoolId);

        VolunteerApplication updatedApplication = volunteerApplicationRepository.findByIdWithItems(applicationId)
                .orElse(application);

        return convertToResponse(updatedApplication);
    }

    @Transactional
    public VolunteerApplicationResponse reorderItems(String email, Long applicationId, ReorderItemsRequest request) {
        User user = getUserByEmail(email);
        VolunteerApplication application = volunteerApplicationRepository.findByIdAndUserId(applicationId, user.getId())
                .orElseThrow(() -> new BusinessException(404, "志愿填报申请不存在"));

        if (application.getStatus() == VolunteerApplicationStatus.SUBMITTED) {
            throw new BusinessException(400, "已提交的志愿不能修改");
        }

        for (ReorderItemsRequest.ReorderItem item : request.getItems()) {
            VolunteerApplicationItem applicationItem = itemRepository
                    .findByVolunteerApplicationIdAndSchoolId(applicationId, item.getSchoolId())
                    .orElseThrow(() -> new BusinessException(404, "学校ID " + item.getSchoolId() + " 不在志愿列表中"));

            applicationItem.setPriority(item.getNewPriority());
            itemRepository.save(applicationItem);
        }

        VolunteerApplication updatedApplication = volunteerApplicationRepository.findByIdWithItems(applicationId)
                .orElse(application);

        return convertToResponse(updatedApplication);
    }

    @Transactional
    public VolunteerApplicationResponse submitApplication(String email, Long applicationId) {
        User user = getUserByEmail(email);
        VolunteerApplication application = volunteerApplicationRepository.findByIdAndUserId(applicationId, user.getId())
                .orElseThrow(() -> new BusinessException(404, "志愿填报申请不存在"));

        if (application.getStatus() == VolunteerApplicationStatus.SUBMITTED) {
            throw new BusinessException(400, "志愿已提交，不能重复提交");
        }

        if (application.getStatus() == VolunteerApplicationStatus.SIMULATION) {
            throw new BusinessException(400, "模拟方案不能提交，请创建正式志愿");
        }

        long itemCount = itemRepository.countByVolunteerApplicationId(applicationId);
        if (itemCount == 0) {
            throw new BusinessException(400, "志愿列表为空，无法提交");
        }

        application.setStatus(VolunteerApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());

        VolunteerApplication submittedApplication = volunteerApplicationRepository.save(application);

        return convertToResponse(submittedApplication);
    }

    @Transactional
    public VolunteerApplicationResponse createSimulation(String email, VolunteerApplicationRequest request) {
        if (request.getSimulationName() == null || request.getSimulationName().trim().isEmpty()) {
            throw new BusinessException(400, "模拟方案名称不能为空");
        }

        return createApplication(email, request);
    }

    @Transactional
    public void deleteApplication(String email, Long applicationId) {
        User user = getUserByEmail(email);
        VolunteerApplication application = volunteerApplicationRepository.findByIdAndUserId(applicationId, user.getId())
                .orElseThrow(() -> new BusinessException(404, "志愿填报申请不存在"));

        if (application.getStatus() == VolunteerApplicationStatus.SUBMITTED) {
            throw new BusinessException(400, "已提交的志愿不能删除");
        }

        volunteerApplicationRepository.delete(application);
    }

    private void addItemToApplication(VolunteerApplication application, VolunteerApplicationItemRequest request) {
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new BusinessException(404, "学校不存在"));

        BigDecimal probability = calculateAdmissionProbability(application.getUser().getId(), school);

        VolunteerApplicationItem item = VolunteerApplicationItem.builder()
                .volunteerApplication(application)
                .school(school)
                .priority(request.getPriority())
                .admissionProbability(probability)
                .build();

        application.addItem(item);
        itemRepository.save(item);
    }

    private BigDecimal calculateAdmissionProbability(Long userId, School school) {
        return studentScoreRepository.findByUserId(userId)
                .map(score -> {
                    BigDecimal studentScore = score.getTotalScore();

                    BigDecimal avgAdmissionScore = calculateAverageAdmissionScore(school);
                    if (avgAdmissionScore == null) {
                        return new BigDecimal("50.00");
                    }

                    int comparison = studentScore.compareTo(avgAdmissionScore);

                    if (comparison > 0) {
                        BigDecimal diff = studentScore.subtract(avgAdmissionScore);
                        BigDecimal bonus = diff.divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("10"));
                        BigDecimal probability = new BigDecimal("70").add(bonus);
                        return probability.min(new BigDecimal("99")).max(new BigDecimal("50"));
                    } else if (comparison < 0) {
                        BigDecimal diff = avgAdmissionScore.subtract(studentScore);
                        BigDecimal penalty = diff.divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("10"));
                        BigDecimal probability = new BigDecimal("50").subtract(penalty);
                        return probability.max(new BigDecimal("5")).min(new BigDecimal("49"));
                    } else {
                        return new BigDecimal("50.00");
                    }
                })
                .orElse(new BigDecimal("50.00"));
    }

    private BigDecimal calculateAverageAdmissionScore(School school) {
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        if (school.getAdmissionScoreYear1() != null) {
            sum = sum.add(school.getAdmissionScoreYear1());
            count++;
        }
        if (school.getAdmissionScoreYear2() != null) {
            sum = sum.add(school.getAdmissionScoreYear2());
            count++;
        }
        if (school.getAdmissionScoreYear3() != null) {
            sum = sum.add(school.getAdmissionScoreYear3());
            count++;
        }

        if (count == 0) {
            return null;
        }

        return sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }

    private VolunteerApplicationResponse convertToResponse(VolunteerApplication application) {
        List<VolunteerApplicationItemResponse> itemResponses = application.getItems().stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());

        return VolunteerApplicationResponse.builder()
                .id(application.getId())
                .userId(application.getUser().getId())
                .year(application.getYear())
                .status(application.getStatus())
                .simulationName(application.getSimulationName())
                .items(itemResponses)
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .submittedAt(application.getSubmittedAt())
                .build();
    }

    private VolunteerApplicationItemResponse convertItemToResponse(VolunteerApplicationItem item) {
        School school = item.getSchool();
        return VolunteerApplicationItemResponse.builder()
                .id(item.getId())
                .schoolId(school.getId())
                .schoolName(school.getName())
                .schoolType(school.getType() != null ? school.getType().name() : null)
                .city(school.getCity())
                .district(school.getDistrict())
                .admissionScoreYear1(school.getAdmissionScoreYear1())
                .admissionScoreYear2(school.getAdmissionScoreYear2())
                .admissionScoreYear3(school.getAdmissionScoreYear3())
                .priority(item.getPriority())
                .admissionProbability(item.getAdmissionProbability())
                .build();
    }

    private VolunteerApplicationListResponse.VolunteerApplicationSummary convertToSummary(VolunteerApplication application) {
        return VolunteerApplicationListResponse.VolunteerApplicationSummary.builder()
                .id(application.getId())
                .year(application.getYear())
                .status(application.getStatus().name())
                .simulationName(application.getSimulationName())
                .itemCount(application.getItems().size())
                .createdAt(application.getCreatedAt())
                .submittedAt(application.getSubmittedAt())
                .build();
    }
}
