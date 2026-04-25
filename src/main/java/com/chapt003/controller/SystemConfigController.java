package com.chapt003.controller;

import com.chapt003.dto.SystemConfigRequest;
import com.chapt003.dto.SystemConfigResponse;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.SystemConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/system-config")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-系统配置", description = "系统参数配置的增删改查")
public class SystemConfigController {

    @Autowired
    private SystemConfigService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemConfigResponse>>> getAll() {
        List<SystemConfigResponse> configs = service.getAll();
        return ResponseEntity.ok(ApiResponse.success("获取系统配置成功", configs));
    }

    @GetMapping("/{configKey}")
    public ResponseEntity<ApiResponse<SystemConfigResponse>> getByKey(@PathVariable String configKey) {
        SystemConfigResponse config = service.getByKey(configKey);
        return ResponseEntity.ok(ApiResponse.success("获取配置成功", config));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SystemConfigResponse>> createOrUpdate(
            @Valid @RequestBody SystemConfigRequest request) {
        SystemConfigResponse config = service.createOrUpdate(request);
        return ResponseEntity.ok(ApiResponse.success("保存配置成功", config));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("删除配置成功", null));
    }
}
