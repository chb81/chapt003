package com.chapt003.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客服会话数据传输对象
 */
public class CustomerServiceSessionDTO {
    private Long id;
    private Long userId;
    private String username;
    private String sessionStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long agentId;
    private String agentName;
    private String resolutionNote;
    private Integer priority;
    private String category;
    private Integer unreadMessageCount;
    private LocalDateTime lastMessageTime;
    
    // 构造函数
    public CustomerServiceSessionDTO() {}
    
    public CustomerServiceSessionDTO(Long id, Long userId, String username, String sessionStatus) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.sessionStatus = sessionStatus;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    
    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getUnreadMessageCount() { return unreadMessageCount; }
    public void setUnreadMessageCount(Integer unreadMessageCount) { this.unreadMessageCount = unreadMessageCount; }
    
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}