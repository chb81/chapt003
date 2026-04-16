package com.chapt003.dto;

import com.chapt003.entity.enums.VolunteerApplicationStatus;
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
public class VolunteerApplicationResponse {
    private Long id;
    private Long userId;
    private Integer year;
    private VolunteerApplicationStatus status;
    private String simulationName;
    private List<VolunteerApplicationItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;
}
