package com.chapt003.controller;

import com.chapt003.dto.HelpDocumentAdminRequest;
import com.chapt003.dto.HelpDocumentAdminResponse;
import com.chapt003.entity.HelpDocument;
import com.chapt003.entity.enums.HelpDocumentCategory;
import com.chapt003.repository.HelpDocumentRepository;
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

@RestController
@RequestMapping("/v1/admin/help-documents")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-帮助文档管理", description = "帮助文档增删改查、发布/下架")
public class AdminHelpDocumentController {

    @Autowired
    private HelpDocumentRepository helpDocumentRepository;

    @GetMapping
    @Operation(summary = "获取文档列表", description = "分页获取所有帮助文档（含未发布）")
    public ResponseEntity<ApiResponse<Page<HelpDocumentAdminResponse>>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean published) {
        Page<HelpDocument> pageData;
        if (category != null && published != null) {
            pageData = helpDocumentRepository.findByCategoryAndPublishedAndDeletedFalseOrderByCreatedAtDesc(
                    HelpDocumentCategory.valueOf(category), published, PageRequest.of(page, size));
        } else if (published != null) {
            pageData = helpDocumentRepository.findByPublishedAndDeletedFalseOrderByCreatedAtDesc(
                    published, PageRequest.of(page, size));
        } else {
            pageData = helpDocumentRepository.findByDeletedFalseOrderByCreatedAtDesc(
                    PageRequest.of(page, size));
        }
        Page<HelpDocumentAdminResponse> response = pageData.map(this::convertToResponse);
        return ResponseEntity.ok(ApiResponse.success("获取文档列表成功", response));
    }

    @PostMapping
    @Operation(summary = "创建文档", description = "新增帮助文档")
    public ResponseEntity<ApiResponse<HelpDocumentAdminResponse>> create(
            @Valid @RequestBody HelpDocumentAdminRequest request) {
        HelpDocument doc = HelpDocument.builder()
                .title(request.getTitle())
                .category(HelpDocumentCategory.valueOf(request.getCategory()))
                .description(request.getDescription())
                .content(request.getContent())
                .readingTime(request.getReadingTime())
                .published(request.getPublished() != null ? request.getPublished() : false)
                .deleted(false)
                .build();
        doc = helpDocumentRepository.save(doc);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建文档成功", convertToResponse(doc)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文档", description = "修改指定帮助文档")
    public ResponseEntity<ApiResponse<HelpDocumentAdminResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody HelpDocumentAdminRequest request) {
        HelpDocument doc = helpDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文档不存在: " + id));
        doc.setTitle(request.getTitle());
        doc.setCategory(HelpDocumentCategory.valueOf(request.getCategory()));
        doc.setDescription(request.getDescription());
        doc.setContent(request.getContent());
        doc.setReadingTime(request.getReadingTime());
        if (request.getPublished() != null) {
            doc.setPublished(request.getPublished());
        }
        doc = helpDocumentRepository.save(doc);
        return ResponseEntity.ok(ApiResponse.success("更新文档成功", convertToResponse(doc)));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布/下架文档", description = "切换文档发布状态")
    public ResponseEntity<ApiResponse<HelpDocumentAdminResponse>> togglePublish(
            @PathVariable Long id,
            @RequestParam Boolean published) {
        HelpDocument doc = helpDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文档不存在: " + id));
        doc.setPublished(published);
        doc = helpDocumentRepository.save(doc);
        return ResponseEntity.ok(ApiResponse.success(published ? "发布成功" : "已下架", convertToResponse(doc)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文档", description = "软删除帮助文档")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        HelpDocument doc = helpDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文档不存在: " + id));
        doc.setDeleted(true);
        helpDocumentRepository.save(doc);
        return ResponseEntity.ok(ApiResponse.success("删除文档成功", null));
    }

    private HelpDocumentAdminResponse convertToResponse(HelpDocument d) {
        return HelpDocumentAdminResponse.builder()
                .id(d.getId())
                .title(d.getTitle())
                .category(d.getCategory() != null ? d.getCategory().name() : null)
                .description(d.getDescription())
                .content(d.getContent())
                .readingTime(d.getReadingTime())
                .viewCount(d.getViewCount())
                .helpfulCount(d.getHelpfulCount())
                .notHelpfulCount(d.getNotHelpfulCount())
                .published(d.getPublished())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
