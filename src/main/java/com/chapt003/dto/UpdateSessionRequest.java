package com.chapt003.dto;

/**
 * 更新客服会话请求
 */
public class UpdateSessionRequest {
    private String sessionStatus;
    private Long agentId;
    private String resolutionNote;
    private Integer priority;
    private String category;
    
    // 构造函数
    public UpdateSessionRequest() {}
    
    public UpdateSessionRequest(String sessionStatus, Long agentId, String resolutionNote) {
        this.sessionStatus = sessionStatus;
        this.agentId = agentId;
        this.resolutionNote = resolutionNote;
    }
    
    // Getters and Setters
    public String getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }
    
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    
    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}