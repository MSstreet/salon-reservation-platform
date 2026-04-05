package com.salon.admin.reservation.dto;

import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.enums.ReservationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationAdminResponse {

    private final Long id;
    private final Long storeId;
    private final Long staffId;
    private final String staffName;
    private final Long menuId;
    private final String menuName;
    private final Long slotId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final ReservationStatus status;
    private final String customerName;
    private final LocalDateTime createdAt;

    public static ReservationAdminResponse from(Reservation reservation) {
        return new ReservationAdminResponse(
                reservation.getId(),
                reservation.getStore().getId(),
                reservation.getStaff().getId(),
                reservation.getStaff().getName(),
                reservation.getMenu().getId(),
                reservation.getMenu().getName(),
                reservation.getSlot() != null ? reservation.getSlot().getId() : null,
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getStatus(),
                reservation.getCustomerName(),
                reservation.getCreatedAt()
        );
    }
}
