package com.chapt003.controller;

import com.chapt003.dto.NotificationResponse;
import com.chapt003.entity.Notification;
import com.chapt003.repository.NotificationRepository;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/v1/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-通知管理", description = "发送通知、查看通知列表")
public class AdminNotificationController {

    @Autowired
    private com.chapt003.service.NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "获取所有通知", description = "分页获取系统所有通知")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Notification> pageData = notificationRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<NotificationResponse> response = pageData.map(this::toResponse);
        return ResponseEntity.ok(ApiResponse.success("获取通知列表成功", response));
    }

    @PostMapping("/send")
    @Operation(summary = "发送通知", description = "向指定用户发送通知")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String type = (String) body.getOrDefault("type", "SYSTEM");
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        String link = (String) body.getOrDefault("link", null);

        Notification notification = notificationService.createNotification(userId, type, title, content, link);
        return ResponseEntity.ok(ApiResponse.success("发送成功", toResponse(notification)));
    }

    @PostMapping("/send-all")
    @Operation(summary = "全员通知", description = "向所有用户发送通知")
    public ResponseEntity<ApiResponse<Integer>> sendToAll(@RequestBody Map<String, Object> body) {
        String type = (String) body.getOrDefault("type", "SYSTEM");
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        String link = (String) body.getOrDefault("link", null);

        java.util.List<com.chapt003.entity.User> users = userRepository.findAll();
        int count = 0;
        for (com.chapt003.entity.User user : users) {
            notificationService.createNotification(user.getId(), type, title, content, link);
            count++;
        }
        return ResponseEntity.ok(ApiResponse.success("已发送" + count + "条通知", count));
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .content(n.getContent())
                .link(n.getLink())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
                .build();
    }
}
