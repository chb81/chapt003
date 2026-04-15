package com.chapt003.service;

import com.chapt003.entity.AuditLog;
import com.chapt003.entity.User;
import com.chapt003.repository.AuditLogRepository;
import com.chapt003.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void logAction(Long userId, String action, String details, Long operatorId) {
        User user = userRepository.findById(userId).orElse(null);
        User operator = operatorId != null ? userRepository.findById(operatorId).orElse(null) : null;

        AuditLog log = AuditLog.builder()
                .user(user)
                .operator(operator)
                .action(action)
                .details(details)
                .build();

        auditLogRepository.save(log);
    }
}
