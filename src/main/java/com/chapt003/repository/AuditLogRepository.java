package com.chapt003.repository;

import com.chapt003.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:action IS NULL OR a.action = :action) AND " +
            "(:operatorId IS NULL OR a.operator.id = :operatorId) AND " +
            "(:userId IS NULL OR a.user.id = :userId)")
    Page<AuditLog> findWithFilters(
            @Param("action") String action,
            @Param("operatorId") Long operatorId,
            @Param("userId") Long userId,
            Pageable pageable);
}
