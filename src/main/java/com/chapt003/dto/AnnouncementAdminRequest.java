package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementAdminRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "类型不能为空")
    private String type;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String publisher;

    @Builder.Default
    private Integer priority = 0;

    private LocalDateTime publishedAt;
}
