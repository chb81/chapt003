package com.chapt003.controller;

import com.chapt003.dto.HelpDocumentDTO;
import com.chapt003.dto.HelpDocumentDetailDTO;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.HelpDocumentCategory;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.HelpDocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/help-documents")
@Tag(name = "帮助文档", description = "帮助文档浏览、搜索、收藏、反馈等接口")
public class HelpDocumentController {

    @Autowired
    private HelpDocumentService helpDocumentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<HelpDocumentDTO>>> getDocumentsByCategory(
            @PathVariable HelpDocumentCategory category,
            Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<HelpDocumentDTO> documents = helpDocumentService.getDocumentsByCategory(category, userId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HelpDocumentDTO>>> getAllDocuments(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<HelpDocumentDTO> documents = helpDocumentService.searchDocuments(null, userId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HelpDocumentDTO>>> searchDocuments(
            @RequestParam(required = false) String keyword,
            Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<HelpDocumentDTO> documents = helpDocumentService.searchDocuments(keyword, userId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HelpDocumentDetailDTO>> getDocumentDetail(
            @PathVariable Long id,
            Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return helpDocumentService.getDocumentDetail(id, userId)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "文档不存在")));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<Void>> toggleFavorite(
            @PathVariable Long id,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        helpDocumentService.toggleFavorite(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<HelpDocumentDTO>>> getFavoriteDocuments(
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<HelpDocumentDTO> documents = helpDocumentService.getFavoriteDocuments(user.getId());
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<ApiResponse<Void>> submitFeedback(
            @PathVariable Long id,
            @RequestParam Boolean isHelpful,
            Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        helpDocumentService.submitFeedback(id, userId, isHelpful);
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
