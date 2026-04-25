package com.chapt003.controller;

import com.chapt003.repository.*;
import com.chapt003.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/admin/metrics")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-业务指标", description = "系统业务指标统计")
public class BusinessMetricsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private HistoricalAdmissionDataRepository historicalAdmissionDataRepository;

    @GetMapping("/business")
    @Operation(summary = "获取业务指标", description = "获取系统核心业务统计数据")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBusinessMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalUsers", userRepository.count());
        metrics.put("totalSchools", schoolRepository.count());
        metrics.put("totalApplications", volunteerApplicationRepository.count());
        metrics.put("totalStudentProfiles", studentProfileRepository.count());
        metrics.put("totalHistoricalData", historicalAdmissionDataRepository.count());

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        metrics.put("memoryUsedMB", (totalMemory - freeMemory) / 1024 / 1024);
        metrics.put("memoryTotalMB", totalMemory / 1024 / 1024);
        metrics.put("memoryMaxMB", runtime.maxMemory() / 1024 / 1024);

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        metrics.put("heapUsedMB", memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024);
        metrics.put("heapMaxMB", memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024);

        return ResponseEntity.ok(ApiResponse.success("获取业务指标成功", metrics));
    }
}
