package com.chapt003.controller;

import com.chapt003.entity.RecommendationPreference;
import com.chapt003.entity.User;
import com.chapt003.repository.RecommendationPreferenceRepository;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/recommendation-preferences")
public class RecommendationPreferenceController {

    @Autowired
    private RecommendationPreferenceRepository recommendationPreferenceRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<RecommendationPreference>> getPreference(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        RecommendationPreference preference = recommendationPreferenceRepository
                .findActiveByUserId(user.getId())
                .orElse(null);

        return ResponseEntity.ok(ApiResponse.success(preference));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecommendationPreference>> createOrUpdatePreference(
            Principal principal,
            @RequestBody RecommendationPreference preference) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        RecommendationPreference existing = recommendationPreferenceRepository
                .findActiveByUserId(user.getId())
                .orElse(null);

        if (existing != null) {
            existing.setPreferredDistricts(preference.getPreferredDistricts());
            existing.setPreferredSchoolTypes(preference.getPreferredSchoolTypes());
            existing.setPreferredSchoolLevels(preference.getPreferredSchoolLevels());
            existing.setMinProbability(preference.getMinProbability() != null ? preference.getMinProbability() : 30);
            existing.setMaxResults(preference.getMaxResults() != null ? preference.getMaxResults() : 5);
            existing = recommendationPreferenceRepository.save(existing);
            return ResponseEntity.ok(ApiResponse.success(existing));
        } else {
            preference.setUser(user);
            preference.setMinProbability(preference.getMinProbability() != null ? preference.getMinProbability() : 30);
            preference.setMaxResults(preference.getMaxResults() != null ? preference.getMaxResults() : 5);
            preference = recommendationPreferenceRepository.save(preference);
            return ResponseEntity.ok(ApiResponse.success(preference));
        }
    }
}
