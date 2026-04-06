package com.salon.admin.designer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StaffMenuAddRequest {

    @NotNull(message = "메뉴 ID는 필수입니다")
    private Long menuId;
}