package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpDocumentAdminResponse {

    private Long id;
    private String title;
    private String category;
    private String description;
    private String content;
    private Integer readingTime;
    private Integer viewCount;
    private Integer helpfulCount;
    private Integer notHelpfulCount;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
