package com.chapt003.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpDocumentDetailDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("category")
    private String category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("content")
    private String content;

    @JsonProperty("reading_time")
    private Integer readingTime;

    @JsonProperty("view_count")
    private Integer viewCount;

    @JsonProperty("helpful_count")
    private Integer helpfulCount;

    @JsonProperty("not_helpful_count")
    private Integer notHelpfulCount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("is_favorite")
    private Boolean isFavorite;
}
