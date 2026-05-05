package com.chapt003.repository;

import com.chapt003.entity.CustomerServiceSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客服会话数据访问层
 */
@Repository
public interface CustomerServiceSessionRepository extends JpaRepository<CustomerServiceSession, Long> {
    
    /**
     * 查找用户的活跃会话
     */
    List<CustomerServiceSession> findByUserIdAndSessionStatus(Long userId, String status);
    
    /**
     * 查找用户的会话列表
     */
    List<CustomerServiceSession> findByUserIdOrderByStartTimeDesc(Long userId);
    
    /**
     * 查找指定时间后的未处理会话
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.sessionStatus = 'ACTIVE' " +
           "AND (s.agentId IS NULL OR s.priority >= 2) " +
           "AND s.startTime < :thresholdTime ORDER BY s.priority DESC, s.startTime ASC")
    List<CustomerServiceSession> findPendingSessions(@Param("thresholdTime") LocalDateTime thresholdTime);
    
    /**
     * 查找客服人员的会话
     */
    List<CustomerServiceSession> findByAgentIdOrderByStartTimeAsc(Long agentId);
    
    /**
     * 查找客服人员的未完成会话
     */
    List<CustomerServiceSession> findByAgentIdAndSessionStatusNot(Long agentId, String status);
    
    /**
     * 查找用户的最新会话
     */
    @Query("SELECT s FROM CustomerServiceSession s WHERE s.user.id = :userId " +
           "AND s.sessionStatus != 'RESOLVED' ORDER BY s.startTime DESC")
    List<CustomerServiceSession> findRecentSessionsByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的会话数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计活跃会话数量
     */
    long countBySessionStatus(String status);

    Page<CustomerServiceSession> findBySessionStatusOrderByStartTimeDesc(String status, Pageable pageable);
}