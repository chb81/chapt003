package com.chapt003.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeChatLoginResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String email;
    private String mobile;
    private String role;
    private Long expiresIn;
    @JsonProperty("isNewUser")
    private boolean isNewUser;
}