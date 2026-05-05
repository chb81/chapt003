package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpDocumentAdminRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String description;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Integer readingTime;

    @Builder.Default
    private Boolean published = false;
}
