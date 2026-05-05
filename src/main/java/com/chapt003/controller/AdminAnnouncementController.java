package com.chapt003.controller;

import com.chapt003.dto.AnnouncementAdminRequest;
import com.chapt003.dto.AnnouncementAdminResponse;
import com.chapt003.entity.Announcement;
import com.chapt003.entity.UserAnnouncementRead;
import com.chapt003.entity.enums.AnnouncementType;
import com.chapt003.repository.AnnouncementRepository;
import com.chapt003.repository.UserAnnouncementReadRepository;
import com.chapt003.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/announcements")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-公告管理", description = "公告增删改查、统计")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private UserAnnouncementReadRepository userAnnouncementReadRepository;

    @GetMapping
    @Operation(summary = "获取公告列表", description = "分页获取公告列表")
    public ResponseEntity<ApiResponse<Page<AnnouncementAdminResponse>>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        Page<Announcement> pageData;
        if (type != null && !type.isEmpty()) {
            pageData = announcementRepository.findByTypeOrderByCreatedAtDesc(
                    AnnouncementType.valueOf(type), PageRequest.of(page, size));
        } else {
            pageData = announcementRepository.findAll(
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        }
        Page<AnnouncementAdminResponse> response = pageData.map(this::convertToResponse);
        return ResponseEntity.ok(ApiResponse.success("获取公告列表成功", response));
    }

    @PostMapping
    @Operation(summary = "创建公告", description = "新增一条公告")
    public ResponseEntity<ApiResponse<AnnouncementAdminResponse>> create(
            @Valid @RequestBody AnnouncementAdminRequest request) {
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .type(AnnouncementType.valueOf(request.getType()))
                .content(request.getContent())
                .publisher(request.getPublisher())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .publishedAt(request.getPublishedAt() != null ? request.getPublishedAt() : LocalDateTime.now())
                .deleted(false)
                .build();
        announcement = announcementRepository.save(announcement);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建公告成功", convertToResponse(announcement)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公告", description = "修改指定公告")
    public ResponseEntity<ApiResponse<AnnouncementAdminResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementAdminRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在: " + id));
        announcement.setTitle(request.getTitle());
        announcement.setType(AnnouncementType.valueOf(request.getType()));
        announcement.setContent(request.getContent());
        announcement.setPublisher(request.getPublisher());
        if (request.getPriority() != null) {
            announcement.setPriority(request.getPriority());
        }
        if (request.getPublishedAt() != null) {
            announcement.setPublishedAt(request.getPublishedAt());
        }
        announcement = announcementRepository.save(announcement);
        return ResponseEntity.ok(ApiResponse.success("更新公告成功", convertToResponse(announcement)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告", description = "软删除指定公告")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在: " + id));
        announcement.setDeleted(true);
        announcementRepository.save(announcement);
        return ResponseEntity.ok(ApiResponse.success("删除公告成功", null));
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "公告统计", description = "获取公告已读人数统计")
    public ResponseEntity<ApiResponse<Object>> getStats(@PathVariable Long id) {
        long readCount = userAnnouncementReadRepository.countByAnnouncementId(id);
        return ResponseEntity.ok(ApiResponse.success("获取统计成功",
                java.util.Collections.singletonMap("readCount", readCount)));
    }

    private AnnouncementAdminResponse convertToResponse(Announcement a) {
        long readCount = userAnnouncementReadRepository.countByAnnouncementId(a.getId());
        return AnnouncementAdminResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .type(a.getType() != null ? a.getType().name() : null)
                .content(a.getContent())
                .publisher(a.getPublisher())
                .priority(a.getPriority())
                .publishedAt(a.getPublishedAt())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .readCount(readCount)
                .build();
    }
}
