package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度必须在2-20个字符之间")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotNull(message = "出生日期不能为空")
    @PastOrPresent(message = "出生日期不能晚于今天")
    private LocalDate birthDate;

    @Size(max = 50, message = "城市名称不能超过50个字符")
    private String city;

    @Size(max = 50, message = "区县名称不能超过50个字符")
    private String district;

    @Size(max = 100, message = "学校名称不能超过100个字符")
    private String school;
}
