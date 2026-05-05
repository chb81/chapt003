package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationPolicyRequest {

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotNull(message = "年度不能为空")
    private Integer year;

    @NotBlank(message = "政策名称不能为空")
    private String policyName;

    @NotBlank(message = "政策类型不能为空")
    private String policyType;

    private BigDecimal totalQuotaPercentage;

    private BigDecimal minScoreGap;

    private String eligibleConditions;

    private String description;

    @Builder.Default
    private Boolean isActive = true;
}
