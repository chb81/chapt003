package com.chapt003.controller;

import com.chapt003.dto.SmartRecommendationRequest;
import com.chapt003.dto.SmartRecommendationResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.SmartRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/smart-recommendation")
@Tag(name = "智能推荐", description = "基于学生成绩和偏好的学校智能推荐")
public class SmartRecommendationController {

    @Autowired
    private SmartRecommendationService smartRecommendationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/generate")
    @Operation(summary = "生成推荐方案", description = "根据学生成绩、偏好和历史录取数据生成智能学校推荐方案")
    public ResponseEntity<ApiResponse<SmartRecommendationResponse>> generateRecommendations(
            Principal principal,
            @RequestBody SmartRecommendationRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        SmartRecommendationResponse response = smartRecommendationService
                .generateRecommendations(user.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
