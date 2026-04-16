package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerApplicationListResponse {
    private List<VolunteerApplicationSummary> applications;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolunteerApplicationSummary {
        private Long id;
        private Integer year;
        private String status;
        private String simulationName;
        private int itemCount;
        private LocalDateTime createdAt;
        private LocalDateTime submittedAt;
    }
}
