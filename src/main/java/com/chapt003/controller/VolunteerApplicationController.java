package com.chapt003.controller;

import com.chapt003.dto.*;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.VolunteerApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/volunteer-applications")
@Tag(name = "志愿填报", description = "志愿方案创建、添加/删除学校、调整顺序、模拟、提交等接口")
public class VolunteerApplicationController {

    @Autowired
    private VolunteerApplicationService volunteerApplicationService;

    @PostMapping
    @Operation(summary = "创建志愿填报", description = "创建新的志愿填报方案")
    public ResponseEntity<ApiResponse<VolunteerApplicationResponse>> createApplication(
            Principal principal,
            @Valid @RequestBody VolunteerApplicationRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationResponse response = volunteerApplicationService.createApplication(
                principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建志愿填报成功", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取志愿详情", description = "根据ID获取志愿填报方案详情")
    public ResponseEntity<ApiResponse<VolunteerApplicationResponse>> getApplicationById(
            Principal principal,
            @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationResponse response = volunteerApplicationService.getApplicationById(
                principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("获取志愿填报详情成功", response));
    }

    @GetMapping
    @Operation(summary = "获取志愿列表", description = "获取当前用户的志愿填报方案列表，支持分页")
    public ResponseEntity<ApiResponse<VolunteerApplicationListResponse>> getApplications(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationListResponse response = volunteerApplicationService.getApplicationsByUser(
                principal.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success("获取志愿填报列表成功", response));
    }

    @GetMapping("/simulations")
    @Operation(summary = "获取模拟方案列表", description = "获取当前用户的所有模拟方案")
    public ResponseEntity<ApiResponse<VolunteerApplicationListResponse>> getSimulations(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationListResponse response = volunteerApplicationService.getSimulationsByUser(
                principal.getName(), page, size);
        return ResponseEntity.ok(ApiResponse.success("获取模拟方案列表成功", response));
    }

    @GetMapping("/history")
    @Operation(summary = "获取志愿历史", description = "获取当前用户的志愿填报历史记录")
    public ResponseEntity<ApiResponse<List<VolunteerApplicationResponse>>> getHistory(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        List<VolunteerApplicationResponse> response = volunteerApplicationService.getApplicationHistory(
                principal.getName());
        return ResponseEntity.ok(ApiResponse.success("获取志愿填报历史成功", response));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "添加志愿学校", description = "向指定志愿方案中添加一所学校")
    public ResponseEntity<ApiResponse<VolunteerApplicationResponse>> addItem(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody VolunteerApplicationItemRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationResponse response = volunteerApplicationService.addItem(
                principal.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("添加志愿学校成功", response));
    }

    @DeleteMapping("/{id}/items/{schoolId}")
    @Operation(summary = "删除志愿学校", description = "从指定志愿方案中删除一所学校")
    public ResponseEntity<ApiResponse<VolunteerApplicationResponse>> removeItem(
            Principal principal,
            @PathVariable Long id,
            @PathVariable Long schoolId) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationResponse response = volunteerApplicationService.removeItem(
                principal.getName(), id, schoolId);
        return ResponseEntity.ok(ApiResponse.success("删除志愿学校成功", response));
    }

    @PutMapping("/{id}/items/reorder")
    @Operation(summary = "调整志愿顺序", description = "调整志愿方案中学校的排列顺序")
    public ResponseEntity<ApiResponse<VolunteerApplicationResponse>> reorderItems(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ReorderItemsRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationResponse response = volunteerApplicationService.reorderItems(
                principal.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("调整志愿顺序成功", response));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交志愿", description = "提交志愿填报方案，提交后不可修改")
    public ResponseEntity<ApiResponse<VolunteerApplicationResponse>> submitApplication(
            Principal principal,
            @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationResponse response = volunteerApplicationService.submitApplication(
                principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("提交志愿成功", response));
    }

    @PostMapping("/simulations")
    @Operation(summary = "创建模拟方案", description = "创建志愿填报模拟方案，模拟方案不计入正式提交")
    public ResponseEntity<ApiResponse<VolunteerApplicationResponse>> createSimulation(
            Principal principal,
            @Valid @RequestBody VolunteerApplicationRequest request) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        VolunteerApplicationResponse response = volunteerApplicationService.createSimulation(
                principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建模拟方案成功", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除志愿方案", description = "删除指定的志愿填报方案")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            Principal principal,
            @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "请先登录"));
        }
        volunteerApplicationService.deleteApplication(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("删除志愿填报成功", null));
    }
}
