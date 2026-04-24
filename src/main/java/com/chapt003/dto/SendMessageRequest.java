package com.chapt003.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

/**
 * 发送消息请求
 */
public class SendMessageRequest {
    @NotNull(message = "消息内容不能为空")
    @Size(min = 1, max = 2000, message = "消息长度必须在1-2000个字符之间")
    private String content;
    
    @Size(max = 500, message = "附件URL长度不能超过500个字符")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "附件URL格式不正确")
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