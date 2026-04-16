package com.chapt003.dto;

import java.time.LocalDateTime;

/**
 * 客服消息数据传输对象
 */
public class CustomerServiceMessageDTO {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private String messageType;
    private String content;
    private Boolean isRead;
    private LocalDateTime readTime;
    private String attachmentUrl;
    private LocalDateTime messageTime;
    
    // 构造函数
    public CustomerServiceMessageDTO() {}
    
    public CustomerServiceMessageDTO(Long id, Long sessionId, String senderName, String messageType, String content) {
        this.id = id;
        this.sessionId = sessionId;
        this.senderName = senderName;
        this.messageType = messageType;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    
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