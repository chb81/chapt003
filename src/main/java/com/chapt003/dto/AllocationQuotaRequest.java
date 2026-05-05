package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationQuotaRequest {

    @NotNull(message = "目标高中ID不能为空")
    private Long schoolId;

    @NotNull(message = "生源初中名称不能为空")
    private String sourceSchoolName;

    private String sourceSchoolCity;

    private String sourceSchoolDistrict;

    @NotNull(message = "年度不能为空")
    private Integer year;

    @Builder.Default
    private Integer quotaCount = 0;

    private BigDecimal admissionScore;

    private BigDecimal unifiedScore;

    private BigDecimal scoreDifference;

    private String policyRule;
}
