package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderItemsRequest {
    @NotNull(message = "排序项列表不能为空")
    private List<ReorderItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReorderItem {
        @NotNull(message = "学校ID不能为空")
        private Long schoolId;

        @NotNull(message = "新优先级不能为空")
        @Min(value = 1, message = "优先级最小为1")
        @Max(value = 8, message = "优先级最大为8")
        private Integer newPriority;
    }
}
