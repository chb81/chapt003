package com.chapt003.controller;

import com.chapt003.dto.UpdateProfileRequest;
import com.chapt003.dto.UserProfileResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/v1/user")
@Tag(name = "用户管理", description = "用户个人资料查看和更新")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取用户资料", description = "获取当前登录用户的个人资料")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        UserProfileResponse response = userService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", response));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户资料", description = "更新当前登录用户的个人资料")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            Principal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        UserProfileResponse response = userService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("资料更新成功", response));
    }
}
