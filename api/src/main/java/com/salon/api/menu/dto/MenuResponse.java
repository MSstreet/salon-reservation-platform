package com.salon.api.menu.dto;

import com.salon.core.domain.entity.ServiceMenu;
import com.salon.core.domain.enums.MenuStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuResponse {

    private final Long menuId;
    private final String name;
    private final Integer durationMin;
    private final Integer price;
    private final MenuStatus status;

    public static MenuResponse from(ServiceMenu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getDurationMin(),
                menu.getPrice(),
                menu.getStatus()
        );
    }
}