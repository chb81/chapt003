package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerApplicationRequest {
    @NotNull(message = "年份不能为空")
    @Min(value = 2020, message = "年份不能早于2020年")
    @Max(value = 2030, message = "年份不能晚于2030年")
    private Integer year;

    @Size(max = 100, message = "模拟方案名称不能超过100个字符")
    private String simulationName;

    private List<VolunteerApplicationItemRequest> items;
}
