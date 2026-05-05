package com.chapt003.controller;

import com.chapt003.dto.*;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.AllocationPolicyService;
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
import java.util.List;

@RestController
@RequestMapping("/v1/admin/allocation")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-分配生管理", description = "分配生名额、政策配置、分数位次的增删改查")
public class AdminAllocationController {

    @Autowired
    private AllocationPolicyService allocationPolicyService;

    @Autowired
    private com.chapt003.service.ScoreRankService scoreRankService;

    @GetMapping("/quotas")
    @Operation(summary = "获取分配生名额列表", description = "分页查询分配生名额，支持按年度、学校、生源初中筛选")
    public ResponseEntity<ApiResponse<Page<AllocationQuotaResponse>>> getQuotaList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String sourceSchoolName) {
        Page<AllocationQuotaResponse> result = allocationPolicyService.getQuotaList(
                year, schoolId, sourceSchoolName,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "year")));
        return ResponseEntity.ok(ApiResponse.success("获取分配生名额列表成功", result));
    }

    @PostMapping("/quotas")
    @Operation(summary = "创建分配生名额", description = "新增一条分配生名额记录")
    public ResponseEntity<ApiResponse<AllocationQuotaResponse>> createQuota(
            @Valid @RequestBody AllocationQuotaRequest request) {
        AllocationQuotaResponse response = allocationPolicyService.createQuota(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建分配生名额成功", response));
    }

    @PutMapping("/quotas/{id}")
    @Operation(summary = "更新分配生名额", description = "修改指定分配生名额记录")
    public ResponseEntity<ApiResponse<AllocationQuotaResponse>> updateQuota(
            @PathVariable Long id,
            @Valid @RequestBody AllocationQuotaRequest request) {
        AllocationQuotaResponse response = allocationPolicyService.updateQuota(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新分配生名额成功", response));
    }

    @DeleteMapping("/quotas/{id}")
    @Operation(summary = "删除分配生名额", description = "删除指定分配生名额记录")
    public ResponseEntity<ApiResponse<Void>> deleteQuota(@PathVariable Long id) {
        allocationPolicyService.deleteQuota(id);
        return ResponseEntity.ok(ApiResponse.success("删除分配生名额成功", null));
    }

    @PostMapping("/quotas/import")
    @Operation(summary = "批量导入分配生名额", description = "批量导入分配生名额数据")
    public ResponseEntity<ApiResponse<Integer>> batchImportQuotas(
            @Valid @RequestBody List<AllocationQuotaRequest> requests) {
        int count = allocationPolicyService.batchImportQuotas(requests);
        return ResponseEntity.ok(ApiResponse.success("导入成功，共 " + count + " 条", count));
    }

    @GetMapping("/policies")
    @Operation(summary = "获取分配生政策列表", description = "分页查询分配生政策，支持按年度、城市、区县筛选")
    public ResponseEntity<ApiResponse<Page<AllocationPolicyResponse>>> getPolicyList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district) {
        Page<AllocationPolicyResponse> result = allocationPolicyService.getPolicyList(
                year, city, district,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "year")));
        return ResponseEntity.ok(ApiResponse.success("获取分配生政策列表成功", result));
    }

    @PostMapping("/policies")
    @Operation(summary = "创建分配生政策", description = "新增一条分配生政策配置")
    public ResponseEntity<ApiResponse<AllocationPolicyResponse>> createPolicy(
            @Valid @RequestBody AllocationPolicyRequest request) {
        AllocationPolicyResponse response = allocationPolicyService.createPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建分配生政策成功", response));
    }

    @PutMapping("/policies/{id}")
    @Operation(summary = "更新分配生政策", description = "修改指定分配生政策配置")
    public ResponseEntity<ApiResponse<AllocationPolicyResponse>> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody AllocationPolicyRequest request) {
        AllocationPolicyResponse response = allocationPolicyService.updatePolicy(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新分配生政策成功", response));
    }

    @DeleteMapping("/policies/{id}")
    @Operation(summary = "删除分配生政策", description = "删除指定分配生政策配置")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(@PathVariable Long id) {
        allocationPolicyService.deletePolicy(id);
        return ResponseEntity.ok(ApiResponse.success("删除分配生政策成功", null));
    }

    @GetMapping("/score-ranks")
    @Operation(summary = "获取分数位次数据列表", description = "分页查询分数位次数据，支持按城市、年度筛选")
    public ResponseEntity<ApiResponse<Page<ScoreRankMappingResponse>>> getScoreRankList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer year) {
        Page<ScoreRankMappingResponse> result = scoreRankService.getScoreRankList(
                city, year,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "totalScore")));
        return ResponseEntity.ok(ApiResponse.success("获取分数位次数据成功", result));
    }

    @PostMapping("/score-ranks")
    @Operation(summary = "创建分数位次数据", description = "新增一条分数位次记录")
    public ResponseEntity<ApiResponse<ScoreRankMappingResponse>> createScoreRank(
            @Valid @RequestBody ScoreRankMappingRequest request) {
        ScoreRankMappingResponse response = scoreRankService.createScoreRankMapping(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建分数位次数据成功", response));
    }

    @PutMapping("/score-ranks/{id}")
    @Operation(summary = "更新分数位次数据", description = "修改指定分数位次记录")
    public ResponseEntity<ApiResponse<ScoreRankMappingResponse>> updateScoreRank(
            @PathVariable Long id,
            @Valid @RequestBody ScoreRankMappingRequest request) {
        ScoreRankMappingResponse response = scoreRankService.updateScoreRankMapping(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新分数位次数据成功", response));
    }

    @DeleteMapping("/score-ranks/{id}")
    @Operation(summary = "删除分数位次数据", description = "删除指定分数位次记录")
    public ResponseEntity<ApiResponse<Void>> deleteScoreRank(@PathVariable Long id) {
        scoreRankService.deleteScoreRankMapping(id);
        return ResponseEntity.ok(ApiResponse.success("删除分数位次数据成功", null));
    }

    @PostMapping("/score-ranks/import")
    @Operation(summary = "批量导入分数位次数据", description = "批量导入分数位次数据")
    public ResponseEntity<ApiResponse<Integer>> batchImportScoreRanks(
            @Valid @RequestBody List<ScoreRankMappingRequest> requests) {
        int count = scoreRankService.batchImportScoreRankMappings(requests);
        return ResponseEntity.ok(ApiResponse.success("导入成功，共 " + count + " 条", count));
    }
}
