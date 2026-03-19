package com.salon.admin.timeslot.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeSlotGenerateResponse {

    private final int generatedCount;
    private final int skippedCount;

    public static TimeSlotGenerateResponse of(int generatedCount, int skippedCount) {
        return new TimeSlotGenerateResponse(generatedCount, skippedCount);
    }
}