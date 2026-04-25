package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigRequest {
    @NotBlank(message = "配置键不能为空")
    private String configKey;
    @NotBlank(message = "配置值不能为空")
    private String configValue;
    private String description;
}
