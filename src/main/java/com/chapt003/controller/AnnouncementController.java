package com.chapt003.controller;

import com.chapt003.dto.AnnouncementDTO;
import com.chapt003.dto.AnnouncementDetailDTO;
import com.chapt003.entity.Announcement;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.AnnouncementType;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.AnnouncementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/announcements")
@Tag(name = "系统公告", description = "系统公告列表查看、详情、标记已读等接口")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAllAnnouncements(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<AnnouncementDTO> announcements = announcementService.getAllAnnouncements(userId);
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAnnouncementsByType(
            @PathVariable AnnouncementType type,
            Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByType(type, userId);
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getUnreadAnnouncements(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<AnnouncementDTO> announcements = announcementService.getUnreadAnnouncements(user.getId());
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementDetailDTO>> getAnnouncementDetail(
            @PathVariable Long id,
            Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return announcementService.getAnnouncementDetail(id, userId)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "公告不存在")));
    }

    @PostMapping("/{id}/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        announcementService.markAsRead(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
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
