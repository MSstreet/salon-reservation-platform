package com.salon.api.timeslot.dto;

import com.salon.core.domain.entity.TimeSlot;
import com.salon.core.domain.enums.SlotStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TimeSlotResponse {

    private final Long id;
    private final Long staffId;
    private final String staffName;
    private final LocalDate date;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final SlotStatus status;
    private final LocalDateTime heldUntil;

    private TimeSlotResponse(Long id, Long staffId, String staffName, LocalDate date,
                             LocalDateTime startAt, LocalDateTime endAt,
                             SlotStatus status, LocalDateTime heldUntil) {
        this.id = id;
        this.staffId = staffId;
        this.staffName = staffName;
        this.date = date;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.heldUntil = heldUntil;
    }

    public static TimeSlotResponse from(TimeSlot slot) {
        return new TimeSlotResponse(
                slot.getId(),
                slot.getStaff().getId(),
                slot.getStaff().getName(),
                slot.getDate(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getStatus(),
                slot.getHeldUntil()
        );
    }
}
