package com.chapt003.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * 更新客服会话请求
 */
public class UpdateSessionRequest {
    @Pattern(regexp = "^(ACTIVE|CLOSED|RESOLVED)$", message = "会话状态必须是ACTIVE、CLOSED或RESOLVED")
    private String sessionStatus;
    
    private Long agentId;
    
    @Size(max = 500, message = "解决说明长度不能超过500个字符")
    private String resolutionNote;
    
    @Min(value = 1, message = "优先级必须大于等于1")
    @Max(value = 3, message = "优先级必须小于等于3")
    private Integer priority;
    
    @Size(min = 1, max = 100, message = "分类长度必须在1-100个字符之间")
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