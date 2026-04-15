package com.chapt003.dto;

import javax.validation.constraints.NotBlank;

public class WeChatLoginRequest {
    @NotBlank(message = "微信授权码不能为空")
    private String code;

    public WeChatLoginRequest() {
    }

    public WeChatLoginRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}