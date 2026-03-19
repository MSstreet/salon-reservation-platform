package com.salon.admin.schedule.dto;

import com.salon.core.domain.entity.StaffSchedule;
import com.salon.core.domain.enums.ScheduleType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StaffScheduleResponse {

    private final Long scheduleId;
    private final Long staffId;
    private final String staffName;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final ScheduleType type;

    public static StaffScheduleResponse from(StaffSchedule schedule) {
        return new StaffScheduleResponse(
                schedule.getId(),
                schedule.getStaff().getId(),
                schedule.getStaff().getName(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getType()
        );
    }
}