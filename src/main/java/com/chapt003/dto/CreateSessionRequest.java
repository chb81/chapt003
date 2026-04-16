package com.chapt003.dto;

/**
 * 创建客服会话请求
 */
public class CreateSessionRequest {
    private String category;
    private String initialMessage;
    private Integer priority;
    
    // 构造函数
    public CreateSessionRequest() {}
    
    public CreateSessionRequest(String category, String initialMessage, Integer priority) {
        this.category = category;
        this.initialMessage = initialMessage;
        this.priority = priority;
    }
    
    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getInitialMessage() { return initialMessage; }
    public void setInitialMessage(String initialMessage) { this.initialMessage = initialMessage; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}