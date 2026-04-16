package com.chapt003.controller;

import com.chapt003.dto.*;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.VolunteerApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/volunteer-applications")
public class VolunteerApplicationController {

    @Autowired
    private VolunteerApplicationService volunteerApplicationService;

    @PostMapping
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
