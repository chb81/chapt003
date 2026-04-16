package com.chapt003.dto;

import com.chapt003.entity.enums.SchoolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolRequest {
    @NotBlank(message = "学校名称不能为空")
    @Size(max = 100, message = "学校名称不能超过100个字符")
    private String name;

    @NotNull(message = "学校类型不能为空")
    private SchoolType type;

    @Size(max = 50, message = "城市名称不能超过50个字符")
    private String city;

    @Size(max = 50, message = "区县名称不能超过50个字符")
    private String district;

    private BigDecimal admissionScoreYear1;

    private BigDecimal admissionScoreYear2;

    private BigDecimal admissionScoreYear3;

    private String description;

    private String features;

    private Integer enrollmentQuota;

    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    @Size(max = 200, message = "地址不能超过200个字符")
    private String address;
}
