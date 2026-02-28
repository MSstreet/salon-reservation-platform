package com.salon.api.reservation.dto;

import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.enums.ReservationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationResponse {

    private final Long id;
    private final Long storeId;
    private final Long staffId;
    private final String staffName;
    private final Long productId;
    private final String productName;
    private final Long slotId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final ReservationStatus status;
    private final String customerName;
    private final LocalDateTime createdAt;

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStore().getId(),
                reservation.getStaff().getId(),
                reservation.getStaff().getName(),
                reservation.getProduct().getId(),
                reservation.getProduct().getName(),
                reservation.getSlot() != null ? reservation.getSlot().getId() : null,
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getStatus(),
                reservation.getCustomerName(),
                reservation.getCreatedAt()
        );
    }
}
