package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentScoreRequest {
    @NotNull(message = "语文成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal chinese;

    @NotNull(message = "数学成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal math;

    @NotNull(message = "英语成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal english;

    @NotNull(message = "物理成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal physics;

    @NotNull(message = "化学成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal chemistry;

    @NotNull(message = "政治成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal politics;

    @NotNull(message = "历史成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal history;

    @NotNull(message = "地理成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal geography;

    @NotNull(message = "生物成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能为负数")
    @DecimalMax(value = "150.00", message = "成绩不能超过150分")
    private BigDecimal biology;
}
