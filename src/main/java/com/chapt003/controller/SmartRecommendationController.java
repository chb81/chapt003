package com.chapt003.controller;

import com.chapt003.dto.SmartRecommendationRequest;
import com.chapt003.dto.SmartRecommendationResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.SmartRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/smart-recommendation")
public class SmartRecommendationController {

    @Autowired
    private SmartRecommendationService smartRecommendationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/generate")
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
