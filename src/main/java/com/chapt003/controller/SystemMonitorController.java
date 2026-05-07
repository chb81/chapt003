package com.chapt003.controller;

import com.chapt003.dto.SystemPerformanceResponse;
import com.chapt003.repository.TbSchoolRepository;
import com.chapt003.repository.UserRepository;
import com.chapt003.repository.VolunteerApplicationRepository;
import com.chapt003.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/v1/admin/system-monitor")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-系统监控", description = "系统性能指标监控（内存、线程、JVM等）")
public class SystemMonitorController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TbSchoolRepository tbSchoolRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<SystemPerformanceResponse>> getPerformance() {
        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();

        SystemPerformanceResponse response = SystemPerformanceResponse.builder()
                .memory(SystemPerformanceResponse.MemoryInfo.builder()
                        .total(totalMemory)
                        .used(usedMemory)
                        .free(freeMemory)
                        .usagePercent(totalMemory > 0 ? (double) usedMemory / totalMemory * 100 : 0)
                        .build())
                .jvm(SystemPerformanceResponse.JvmInfo.builder()
                        .javaVersion(System.getProperty("java.version"))
                        .javaVendor(System.getProperty("java.vendor"))
                        .maxMemory(maxMemory)
                        .totalMemory(totalMemory)
                        .freeMemory(freeMemory)
                        .heapUsagePercent(heapMax > 0 ? (double) heapUsed / heapMax * 100 : 0)
                        .build())
                .uptime(runtimeBean.getUptime())
                .startTime(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(runtimeBean.getStartTime()),
                        ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .activeThreads(threadBean.getThreadCount())
                .totalUsers(userRepository.count())
                .totalSchools(tbSchoolRepository.count())
                .totalApplications(volunteerApplicationRepository.count())
                .build();

        return ResponseEntity.ok(ApiResponse.success("获取系统性能数据成功", response));
    }
}
