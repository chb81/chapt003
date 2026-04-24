package com.chapt003.controller;

import com.chapt003.dto.AdmissionProbabilityDetailResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.AdmissionProbabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/v1/admission-probability")
@Tag(name = "录取概率预测", description = "根据学生成绩和学校历史录取数据预测录取概率")
public class AdmissionProbabilityController {

    @Autowired
    private AdmissionProbabilityService admissionProbabilityService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/calculate")
    @Operation(summary = "计算录取概率", description = "根据当前登录学生的成绩信息和目标学校的历史录取数据，计算录取概率")
    public ResponseEntity<ApiResponse<AdmissionProbabilityDetailResponse>> calculateProbability(
            Principal principal,
            @RequestParam Long schoolId) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        AdmissionProbabilityDetailResponse response = admissionProbabilityService
                .calculateProbability(user.getId(), schoolId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
