package com.chapt003.dto;

import com.chapt003.entity.enums.AnnouncementType;
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
public class AnnouncementDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("type")
    private String type;

    @JsonProperty("content")
    private String content;

    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("published_at")
    private LocalDateTime publishedAt;

    @JsonProperty("is_read")
    private Boolean isRead;
}
