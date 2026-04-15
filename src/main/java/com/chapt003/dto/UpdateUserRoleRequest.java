package com.chapt003.dto;

import com.chapt003.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
    @NotNull(message = "角色不能为空")
    private UserRole role;
}
