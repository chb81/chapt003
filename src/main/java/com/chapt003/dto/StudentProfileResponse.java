package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String city;
    private String district;
    private String school;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
