package com.chapt003.controller;

import com.chapt003.dto.LoginRequest;
import com.chapt003.dto.LoginResponse;
import com.chapt003.dto.RegisterRequest;
import com.chapt003.dto.RegisterResponse;
import com.chapt003.dto.VerificationRequest;
import com.chapt003.dto.WeChatLoginRequest;
import com.chapt003.dto.WeChatLoginResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.AuthService;
import com.chapt003.service.WeChatOAuthService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private WeChatOAuthService weChatOAuthService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("注册成功", response));
    }
    
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(@Valid @RequestBody VerificationRequest request) {
        boolean verified = authService.verifyEmail(request.getEmail(), request.getCode());
        
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("邮箱验证成功", null));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(20001, "验证码无效或已过期"));
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        authService.resendVerificationCode(email);
        return ResponseEntity.ok(ApiResponse.success("验证码已重新发送", null));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }
    
    @PostMapping("/wechat-login")
    public ResponseEntity<ApiResponse<WeChatLoginResponse>> wechatLogin(@Valid @RequestBody WeChatLoginRequest request) {
        WeChatLoginResponse response = weChatOAuthService.loginWithWeChat(request);
        return ResponseEntity.ok(ApiResponse.success("微信登录成功", response));
    }
}
