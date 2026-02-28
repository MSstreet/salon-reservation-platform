package com.salon.admin.store.dto;

import com.salon.core.domain.entity.Store;
import com.salon.core.domain.enums.StoreStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StoreResponse {

    private final Long id;
    private final String name;
    private final StoreStatus status;
    private final String timezone;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private StoreResponse(Long id, String name, StoreStatus status, String timezone,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.timezone = timezone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getStatus(),
                store.getTimezone(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }
}
