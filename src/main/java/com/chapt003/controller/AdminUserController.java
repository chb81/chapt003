package com.chapt003.controller;

import com.chapt003.dto.*;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserListResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status) {
        Page<UserListResponse> response = userService.getUserList(page, size, search, role, status);
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@PathVariable Long userId) {
        UserDetailResponse response = userService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResponse.success("获取用户详情成功", response));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request,
            Principal principal) {
        userService.updateUserRole(userId, request.getRole(), principal.getName());
        return ResponseEntity.ok(ApiResponse.success("角色更新成功", null));
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request,
            Principal principal) {
        userService.updateUserStatus(userId, request.getStatus(), principal.getName());
        String message = request.getStatus() == UserStatus.DISABLED ? "用户已禁用" : "用户状态更新成功";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId, Principal principal) {
        userService.deleteUser(userId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("用户已删除", null));
    }
}
