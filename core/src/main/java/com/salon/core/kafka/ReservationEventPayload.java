package com.salon.core.kafka;

import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.enums.EventType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationEventPayload {

    private Long reservationId;
    private Long storeId;
    private Long staffId;
    private String customerName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private EventType eventType;
    private LocalDateTime occurredAt;

    private ReservationEventPayload(Reservation reservation, EventType eventType) {
        this.reservationId = reservation.getId();
        this.storeId = reservation.getStore().getId();
        this.staffId = reservation.getStaff().getId();
        this.customerName = reservation.getCustomerName();
        this.startAt = reservation.getStartAt();
        this.endAt = reservation.getEndAt();
        this.eventType = eventType;
        this.occurredAt = LocalDateTime.now();
    }

    public static ReservationEventPayload of(Reservation reservation, EventType eventType) {
        return new ReservationEventPayload(reservation, eventType);
    }
}