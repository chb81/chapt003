package com.chapt003.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 客服会话实体
 */
@Entity
@Table(name = "customer_service_sessions")
public class CustomerServiceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "session_status", nullable = false)
    private String sessionStatus = "ACTIVE"; // ACTIVE, CLOSED, RESOLVED
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "agent_id")
    private Long agentId;
    
    @Column(name = "resolution_note")
    private String resolutionNote;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerServiceMessage> messages = new ArrayList<>();
    
    @Column(name = "priority")
    private Integer priority = 1; // 1-低, 2-中, 3-高
    
    @Column(name = "category")
    private String category;
    
    // 构造函数
    public CustomerServiceSession() {
        this.startTime = LocalDateTime.now();
    }
    
    public CustomerServiceSession(User user) {
        this();
        this.user = user;
    }
    
    // 添加消息
    public void addMessage(CustomerServiceMessage message) {
        message.setSession(this);
        this.messages.add(message);
    }
    
    // 关闭会话
    public void closeSession(String resolutionNote) {
        this.sessionStatus = "CLOSED";
        this.endTime = LocalDateTime.now();
        this.resolutionNote = resolutionNote;
    }
    
    // 解决会话
    public void resolveSession(String resolutionNote) {
        this.sessionStatus = "RESOLVED";
        this.endTime = LocalDateTime.now();
        this.resolutionNote = resolutionNote;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    
    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }
    
    public List<CustomerServiceMessage> getMessages() { return messages; }
    public void setMessages(List<CustomerServiceMessage> messages) { this.messages = messages; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}