package com.chapt003.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 客服消息实体
 */
@Entity
@Table(name = "customer_service_messages")
public class CustomerServiceMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CustomerServiceSession session;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @Column(name = "message_type", nullable = false)
    private String messageType; // USER_MESSAGE, AGENT_MESSAGE, SYSTEM_MESSAGE
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    @Column(name = "attachment_url")
    private String attachmentUrl;
    
    @Column(name = "message_time", nullable = false)
    private LocalDateTime messageTime;
    
    // 构造函数
    public CustomerServiceMessage() {
        this.messageTime = LocalDateTime.now();
    }
    
    public CustomerServiceMessage(CustomerServiceSession session, User sender, String messageType, String content) {
        this();
        this.session = session;
        this.sender = sender;
        this.messageType = messageType;
        this.content = content;
    }
    
    // 标记为已读
    public void markAsRead() {
        this.isRead = true;
        this.readTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public CustomerServiceSession getSession() { return session; }
    public void setSession(CustomerServiceSession session) { this.session = session; }
    
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }
    
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    
    public LocalDateTime getMessageTime() { return messageTime; }
    public void setMessageTime(LocalDateTime messageTime) { this.messageTime = messageTime; }
}