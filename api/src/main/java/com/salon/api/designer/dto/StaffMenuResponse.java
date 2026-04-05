package com.salon.api.designer.dto;

import com.salon.core.domain.entity.StaffMenu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StaffMenuResponse {

    private final Long menuId;
    private final String menuName;
    private final Integer durationMin;
    private final Integer price;

    public static StaffMenuResponse from(StaffMenu staffMenu) {
        return new StaffMenuResponse(
                staffMenu.getMenu().getId(),
                staffMenu.getMenu().getName(),
                staffMenu.getMenu().getDurationMin(),
                staffMenu.getMenu().getPrice()
        );
    }
}