package com.chapt003.controller;

import com.chapt003.dto.OnboardingStatusResponse;
import com.chapt003.dto.ShareResponse;
import com.chapt003.dto.NotificationResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.NotificationService;
import com.chapt003.service.ShareService;
import com.chapt003.service.UserOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user-experience")
@Tag(name = "用户体验", description = "引导、通知、分享等用户体验功能")
public class UserExperienceController {

    @Autowired
    private UserOnboardingService onboardingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/onboarding/status")
    @Operation(summary = "获取引导状态", description = "查看当前用户的引导完成进度")
    public ResponseEntity<ApiResponse<OnboardingStatusResponse>> getOnboardingStatus(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("获取引导状态成功",
                onboardingService.getOnboardingStatus(userId)));
    }

    @PostMapping("/onboarding/complete-step")
    @Operation(summary = "完成引导步骤", description = "标记某个引导步骤为已完成")
    public ResponseEntity<ApiResponse<OnboardingStatusResponse>> completeStep(
            Principal principal,
            @RequestBody Map<String, Integer> body) {
        Long userId = getUserIdFromPrincipal(principal);
        Integer step = body.get("step");
        return ResponseEntity.ok(ApiResponse.success("步骤完成",
                onboardingService.completeStep(userId, step)));
    }

    @GetMapping("/notifications")
    @Operation(summary = "获取通知列表", description = "分页获取当前用户的通知列表")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("获取通知成功",
                notificationService.getNotifications(userId, page, size)));
    }

    @GetMapping("/notifications/unread")
    @Operation(summary = "获取未读通知", description = "获取所有未读通知")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("获取未读通知成功",
                notificationService.getUnreadNotifications(userId)));
    }

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "未读通知数", description = "获取未读通知数量")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("获取成功",
                notificationService.getUnreadCount(userId)));
    }

    @PostMapping("/notifications/{id}/read")
    @Operation(summary = "标记已读", description = "标记某条通知为已读")
    public ResponseEntity<ApiResponse<Void>> markAsRead(Principal principal, @PathVariable Long id) {
        Long userId = getUserIdFromPrincipal(principal);
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("已标记为已读", null));
    }

    @PostMapping("/notifications/read-all")
    @Operation(summary = "全部标记已读", description = "将所有未读通知标记为已读")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("全部已标记为已读", null));
    }

    @PostMapping("/share")
    @Operation(summary = "创建分享", description = "创建志愿方案/学校的分享链接")
    public ResponseEntity<ApiResponse<ShareResponse>> createShare(
            Principal principal,
            @RequestBody Map<String, String> body) {
        Long userId = getUserIdFromPrincipal(principal);
        String shareType = body.get("shareType");
        String targetIdStr = body.get("targetId");
        String title = body.get("title");
        String description = body.get("description");
        Long targetId = targetIdStr != null ? Long.parseLong(targetIdStr) : null;
        return ResponseEntity.ok(ApiResponse.success("分享创建成功",
                shareService.createShare(userId, shareType, targetId, title, description)));
    }

    @GetMapping("/share/{shareCode}")
    @Operation(summary = "查看分享", description = "通过分享码查看分享内容")
    public ResponseEntity<ApiResponse<ShareResponse>> getShare(@PathVariable String shareCode) {
        return ResponseEntity.ok(ApiResponse.success("获取分享成功",
                shareService.getShareByCode(shareCode)));
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null ? user.getId() : null;
    }
}
