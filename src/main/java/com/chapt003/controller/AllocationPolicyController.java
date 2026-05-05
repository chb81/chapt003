package com.chapt003.controller;

import com.chapt003.dto.AllocationPolicyResponse;
import com.chapt003.dto.AllocationQuotaResponse;
import com.chapt003.entity.User;
import com.chapt003.repository.UserRepository;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.AllocationPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/allocation")
@Tag(name = "分配生/指标生政策", description = "分配生名额查询、政策查询、概率修正")
public class AllocationPolicyController {

    @Autowired
    private AllocationPolicyService allocationPolicyService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/schools/{schoolId}/quotas")
    @Operation(summary = "查看目标高中的分配生名额", description = "查看某所高中对各初中的分配生/指标生名额分配")
    public ResponseEntity<ApiResponse<List<AllocationQuotaResponse>>> getQuotasBySchool(
            @PathVariable Long schoolId,
            @RequestParam(required = false) Integer year) {
        List<AllocationQuotaResponse> quotas = allocationPolicyService.getQuotasBySchool(schoolId, year);
        return ResponseEntity.ok(ApiResponse.success("获取分配生名额成功", quotas));
    }

    @GetMapping("/source-school")
    @Operation(summary = "查看学生所在初中的分配生机会", description = "根据学生所在初中查询可用的分配生名额")
    public ResponseEntity<ApiResponse<List<AllocationQuotaResponse>>> getQuotasBySourceSchool(
            @RequestParam String sourceSchoolName,
            @RequestParam(required = false) Integer year) {
        List<AllocationQuotaResponse> quotas = allocationPolicyService.getQuotasBySourceSchool(sourceSchoolName, year);
        return ResponseEntity.ok(ApiResponse.success("获取分配生机会成功", quotas));
    }

    @GetMapping("/my-options")
    @Operation(summary = "查看我的分配生选择", description = "查看当前学生可享受的所有分配生名额")
    public ResponseEntity<ApiResponse<List<AllocationQuotaResponse>>> getMyAllocationOptions(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("获取分配生选择成功",
                allocationPolicyService.getStudentAllocationOptions(userId)));
    }

    @GetMapping("/policies")
    @Operation(summary = "查看区县分配生政策", description = "查看指定区县的分配生政策规则")
    public ResponseEntity<ApiResponse<List<AllocationPolicyResponse>>> getPolicies(
            @RequestParam String city,
            @RequestParam String district,
            @RequestParam(required = false) Integer year) {
        List<AllocationPolicyResponse> policies = allocationPolicyService.getPoliciesByCityDistrict(city, district, year);
        return ResponseEntity.ok(ApiResponse.success("获取分配生政策成功", policies));
    }

    @GetMapping("/advantage")
    @Operation(summary = "检查分配生优势", description = "检查当前学生在目标高中是否有分配生优势")
    public ResponseEntity<ApiResponse<Boolean>> checkAdvantage(
            Principal principal,
            @RequestParam Long schoolId) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success("检查完成",
                allocationPolicyService.hasAllocationAdvantage(userId, schoolId)));
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
