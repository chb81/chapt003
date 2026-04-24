package com.chapt003.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 创建客服会话请求
 */
public class CreateSessionRequest {
    @Size(min = 1, max = 100, message = "分类长度必须在1-100个字符之间")
    private String category;
    
    @Size(min = 1, max = 1000, message = "初始消息长度必须在1-1000个字符之间")
    private String initialMessage;
    
    @Min(value = 1, message = "优先级必须大于等于1")
    @Max(value = 3, message = "优先级必须小于等于3")
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