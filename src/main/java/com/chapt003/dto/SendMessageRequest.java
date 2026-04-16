package com.chapt003.dto;

/**
 * 发送消息请求
 */
public class SendMessageRequest {
    private String content;
    private String attachmentUrl;
    
    // 构造函数
    public SendMessageRequest() {}
    
    public SendMessageRequest(String content, String attachmentUrl) {
        this.content = content;
        this.attachmentUrl = attachmentUrl;
    }
    
    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
}