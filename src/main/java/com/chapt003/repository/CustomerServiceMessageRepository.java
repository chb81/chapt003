package com.chapt003.repository;

import com.chapt003.entity.CustomerServiceMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客服消息数据访问层
 */
@Repository
public interface CustomerServiceMessageRepository extends JpaRepository<CustomerServiceMessage, Long> {
    
    /**
     * 查找会话的所有消息
     */
    List<CustomerServiceMessage> findBySessionIdOrderByMessageTimeAsc(Long sessionId);
    
    /**
     * 查找会话中未读的消息
     */
    @Query("SELECT m FROM CustomerServiceMessage m WHERE m.session.id = :sessionId AND m.isRead = false")
    List<CustomerServiceMessage> findUnreadMessagesBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 查找用户发送的消息
     */
    List<CustomerServiceMessage> findBySenderId(Long senderId);
    
    /**
     * 查找用户在指定会话中的消息
     */
    List<CustomerServiceMessage> findBySessionIdAndSenderId(Long sessionId, Long senderId);
    
    /**
     * 查找客服回复的消息
     */
    @Query("SELECT m FROM CustomerServiceMessage m WHERE m.messageType = 'AGENT_MESSAGE' " +
           "AND m.sender.role = 'CUSTOMER_SERVICE' ORDER BY m.messageTime DESC")
    List<CustomerServiceMessage> findAgentReplies();
    
    /**
     * 查找指定时间后的新消息
     */
    @Query("SELECT m FROM CustomerServiceMessage m WHERE m.session.id = :sessionId " +
           "AND m.messageTime > :lastMessageTime ORDER BY m.messageTime ASC")
    List<CustomerServiceMessage> findNewMessagesAfterTime(@Param("sessionId") Long sessionId, 
                                                         @Param("lastMessageTime") LocalDateTime lastMessageTime);
    
    /**
     * 统计会话中的未读消息数量
     */
    @Query("SELECT COUNT(m) FROM CustomerServiceMessage m WHERE m.session.id = :sessionId AND m.isRead = false")
    long countUnreadMessagesBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 标记会话中所有消息为已读
     */
    @Query("UPDATE CustomerServiceMessage m SET m.isRead = true, m.readTime = :readTime " +
           "WHERE m.session.id = :sessionId AND m.isRead = false")
    int markAllMessagesAsRead(@Param("sessionId") Long sessionId, @Param("readTime") LocalDateTime readTime);
}