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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "认证管理", description = "用户注册、登录、邮箱验证等认证接口")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private WeChatOAuthService weChatOAuthService;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号，注册后需进行邮箱验证")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("注册成功", response));
    }
    
    @PostMapping("/verify")
    @Operation(summary = "邮箱验证", description = "验证用户注册邮箱")
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
    @Operation(summary = "重发验证码", description = "重新发送邮箱验证码")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        authService.resendVerificationCode(email);
        return ResponseEntity.ok(ApiResponse.success("验证码已重新发送", null));
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用邮箱和密码登录，返回JWT令牌")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }
    
    @PostMapping("/wechat-login")
    @Operation(summary = "微信登录", description = "使用微信授权码登录")
    public ResponseEntity<ApiResponse<WeChatLoginResponse>> wechatLogin(@Valid @RequestBody WeChatLoginRequest request) {
        WeChatLoginResponse response = weChatOAuthService.loginWithWeChat(request);
        return ResponseEntity.ok(ApiResponse.success("微信登录成功", response));
    }
}
