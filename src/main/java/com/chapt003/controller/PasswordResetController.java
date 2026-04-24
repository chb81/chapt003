package com.chapt003.controller;

import com.chapt003.dto.PasswordResetSendCodeRequest;
import com.chapt003.dto.PasswordResetVerifyRequest;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.PasswordResetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth/password-reset")
@Tag(name = "密码重置", description = "密码找回和重置接口")
public class PasswordResetController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@Valid @RequestBody PasswordResetSendCodeRequest request) {
        passwordResetService.sendVerificationCode(request.getEmail(), request.getMobile());
        return ResponseEntity.ok(ApiResponse.success("验证码已发送", null));
    }
    
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyAndResetPassword(@Valid @RequestBody PasswordResetVerifyRequest request) {
        passwordResetService.resetPassword(
            request.getEmail(),
            request.getMobile(),
            request.getVerificationCode(),
            request.getNewPassword(),
            request.getConfirmPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("密码重置成功", null));
    }
}
