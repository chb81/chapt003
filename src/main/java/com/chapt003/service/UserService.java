package com.chapt003.service;

import com.chapt003.dto.*;
import com.chapt003.entity.AuditLog;
import com.chapt003.entity.LoginHistory;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.exception.BusinessException;
import com.chapt003.exception.DuplicateUserException;
import com.chapt003.repository.AuditLogRepository;
import com.chapt003.repository.LoginHistoryRepository;
import com.chapt003.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        return convertToResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String currentUsername, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateUserException("邮箱", request.getEmail());
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false);
            verificationService.generateAndSendVerificationCode(request.getEmail());
        }

        if (request.getMobile() != null && !request.getMobile().equals(user.getMobile())) {
            if (userRepository.existsByMobile(request.getMobile())) {
                throw new DuplicateUserException("手机号", request.getMobile());
            }
            user.setMobile(request.getMobile());
            user.setMobileVerified(false);
        }

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    public Page<UserListResponse> getUserList(int page, int size, String search, UserRole role, UserStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAllAdmin(search, role, status, pageable);
        return userPage.map(this::convertToListResponse);
    }

    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        Pageable historyPageable = PageRequest.of(0, 10);
        List<LoginHistory> histories = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId, historyPageable).getContent();
        List<AuditLog> logs = auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, historyPageable).getContent();

        return UserDetailResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .emailVerified(user.getEmailVerified())
                .mobileVerified(user.getMobileVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .loginHistory(histories.stream().map(h -> UserDetailResponse.LoginHistoryInfo.builder()
                        .loginTime(h.getLoginTime())
                        .ipAddress(h.getIpAddress())
                        .userAgent(h.getUserAgent())
                        .loginMethod(h.getLoginMethod().name())
                        .build()).collect(Collectors.toList()))
                .auditLogs(logs.stream().map(l -> UserDetailResponse.AuditLogInfo.builder()
                        .id(l.getId())
                        .action(l.getAction())
                        .details(l.getDetails())
                        .createdAt(l.getCreatedAt())
                        .operatorName(l.getOperator() != null ? l.getOperator().getUsername() : "SYSTEM")
                        .build()).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void updateUserRole(Long userId, UserRole newRole, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException(404, "管理员不存在"));

        if (user.getId().equals(admin.getId())) {
            throw new BusinessException(400, "管理员不能修改自己的角色");
        }

        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        userRepository.save(user);

        auditLogService.logAction(userId, "UPDATE_ROLE", 
                String.format("Role changed from %s to %s", oldRole, newRole), admin.getId());
    }

    @Transactional
    public void updateUserStatus(Long userId, UserStatus newStatus, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException(404, "管理员不存在"));

        if (user.getId().equals(admin.getId())) {
            throw new BusinessException(400, "管理员不能禁用或删除自己");
        }

        if (newStatus == UserStatus.DELETED || newStatus == UserStatus.DISABLED) {
            // Check if it's the last admin
            if (user.getRole() == UserRole.ADMIN) {
                long adminCount = userRepository.countByRoleAndStatus(UserRole.ADMIN, UserStatus.ACTIVE);
                if (adminCount <= 1) {
                    throw new BusinessException(400, "系统必须保留至少一个活跃的管理员账户");
                }
            }
        }

        UserStatus oldStatus = user.getStatus();
        user.setStatus(newStatus);
        if (newStatus == UserStatus.DELETED) {
            user.setDeletedAt(LocalDateTime.now());
        }
        userRepository.save(user);

        auditLogService.logAction(userId, "UPDATE_STATUS", 
                String.format("Status changed from %s to %s", oldStatus, newStatus), admin.getId());
    }

    @Transactional
    public void deleteUser(Long userId, String adminEmail) {
        updateUserStatus(userId, UserStatus.DELETED, adminEmail);
    }

    private UserProfileResponse convertToResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .emailVerified(user.getEmailVerified())
                .mobileVerified(user.getMobileVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserListResponse convertToListResponse(User user) {
        return UserListResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}

