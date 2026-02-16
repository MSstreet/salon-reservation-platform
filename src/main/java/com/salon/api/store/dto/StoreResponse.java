package com.salon.api.store.dto;

import com.salon.domain.entity.Store;
import com.salon.domain.enums.StoreStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StoreResponse {

    private Long id;
    private String name;
    private StoreStatus status;
    private String timezone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StoreResponse from(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .status(store.getStatus())
                .timezone(store.getTimezone())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .build();
    }
}
