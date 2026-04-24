package com.chapt003.entity;

import com.chapt003.entity.enums.SessionStatus;
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
    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus = SessionStatus.ACTIVE;
    
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
    public synchronized void closeSession(String resolutionNote) {
        if (this.sessionStatus != SessionStatus.ACTIVE) {
            throw new IllegalStateException("只能在活跃状态下关闭会话");
        }
        this.sessionStatus = SessionStatus.CLOSED;
        this.endTime = LocalDateTime.now();
        this.resolutionNote = resolutionNote;
    }
    
    // 解决会话
    public synchronized void resolveSession(String resolutionNote) {
        if (this.sessionStatus != SessionStatus.ACTIVE) {
            throw new IllegalStateException("只能在活跃状态下解决会话");
        }
        this.sessionStatus = SessionStatus.RESOLVED;
        this.endTime = LocalDateTime.now();
        this.resolutionNote = resolutionNote;
    }
    
    // 分配客服人员
    public synchronized void assignAgent(Long agentId) {
        if (this.sessionStatus != SessionStatus.ACTIVE) {
            throw new IllegalStateException("只能在活跃状态下分配客服");
        }
        if (this.agentId != null) {
            throw new IllegalStateException("会话已经分配给其他客服");
        }
        this.agentId = agentId;
    }
    
    // 检查是否可以分配客服
    public synchronized boolean canAssignAgent() {
        return this.sessionStatus == SessionStatus.ACTIVE && this.agentId == null;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public SessionStatus getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(SessionStatus sessionStatus) { this.sessionStatus = sessionStatus; }
    
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