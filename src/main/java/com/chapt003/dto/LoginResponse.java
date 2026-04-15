package com.chapt003.dto;

public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String email;
    private String mobile;
    private String role;
    private Long expiresIn;

    public LoginResponse() {
    }

    public LoginResponse(String token, String refreshToken, Long userId, String email, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LoginResponse response = new LoginResponse();

        public Builder token(String token) {
            response.setToken(token);
            return this;
        }

        public Builder userId(Long userId) {
            response.setUserId(userId);
            return this;
        }

        public Builder email(String email) {
            response.setEmail(email);
            return this;
        }

        public Builder mobile(String mobile) {
            response.setMobile(mobile);
            return this;
        }

        public Builder role(String role) {
            response.setRole(role);
            return this;
        }

        public Builder expiresIn(Long expiresIn) {
            response.setExpiresIn(expiresIn);
            return this;
        }

        public LoginResponse build() {
            return response;
        }
    }
}