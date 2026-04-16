package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerApplicationItemRequest {
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @NotNull(message = "志愿优先级不能为空")
    @Min(value = 1, message = "志愿优先级最小为1")
    @Max(value = 8, message = "志愿优先级最大为8")
    private Integer priority;
}
