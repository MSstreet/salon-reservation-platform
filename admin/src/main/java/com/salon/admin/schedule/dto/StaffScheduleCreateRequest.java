package com.salon.admin.schedule.dto;

import com.salon.core.domain.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "근무 스케줄 등록 요청")
@Getter
@NoArgsConstructor
public class StaffScheduleCreateRequest {

    @Schema(description = "디자이너(스태프) ID", example = "1")
    @NotNull
    private Long staffId;

    @Schema(description = "날짜", example = "2025-04-01")
    @NotNull
    private LocalDate date;

    @Schema(description = "근무 시작 시간", example = "10:00")
    @NotNull
    private LocalTime startTime;

    @Schema(description = "근무 종료 시간", example = "19:00")
    @NotNull
    private LocalTime endTime;

    @Schema(description = "스케줄 유형 (WORK | OFF)", example = "WORK")
    @NotNull
    private ScheduleType type;
}