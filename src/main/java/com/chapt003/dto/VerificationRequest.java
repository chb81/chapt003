package com.chapt003.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class VerificationRequest {
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位")
    private String code;

    public VerificationRequest() {
    }

    public VerificationRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}