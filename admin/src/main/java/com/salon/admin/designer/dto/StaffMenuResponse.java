package com.salon.admin.designer.dto;

import com.salon.core.domain.entity.StaffMenu;
import com.salon.core.domain.enums.MenuStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StaffMenuResponse {

    private final Long staffMenuId;
    private final Long menuId;
    private final String menuName;
    private final Integer durationMin;
    private final Integer price;
    private final MenuStatus menuStatus;

    public static StaffMenuResponse from(StaffMenu staffMenu) {
        return new StaffMenuResponse(
                staffMenu.getId(),
                staffMenu.getMenu().getId(),
                staffMenu.getMenu().getName(),
                staffMenu.getMenu().getDurationMin(),
                staffMenu.getMenu().getPrice(),
                staffMenu.getMenu().getStatus()
        );
    }
}