package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPerformanceResponse {
    private MemoryInfo memory;
    private JvmInfo jvm;
    private long uptime;
    private String startTime;
    private int activeThreads;
    private long totalUsers;
    private long totalSchools;
    private long totalApplications;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryInfo {
        private long total;
        private long used;
        private long free;
        private double usagePercent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JvmInfo {
        private String javaVersion;
        private String javaVendor;
        private long maxMemory;
        private long totalMemory;
        private long freeMemory;
        private double heapUsagePercent;
    }
}
