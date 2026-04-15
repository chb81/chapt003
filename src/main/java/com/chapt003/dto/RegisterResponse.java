package com.chapt003.dto;

public class RegisterResponse {
    private Long userId;
    private String email;
    private String mobile;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(Long userId, String email, String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}