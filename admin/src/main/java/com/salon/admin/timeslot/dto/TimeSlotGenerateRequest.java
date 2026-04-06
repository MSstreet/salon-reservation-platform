package com.salon.admin.timeslot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "타임슬롯 생성 요청")
@Getter
@NoArgsConstructor
public class TimeSlotGenerateRequest {

    @Schema(description = "디자이너(스태프) ID", example = "1")
    @NotNull
    private Long staffId;

    @Schema(description = "생성 시작 날짜", example = "2025-04-01")
    @NotNull
    private LocalDate startDate;

    @Schema(description = "생성 종료 날짜 (단일 날짜는 startDate와 동일하게)", example = "2025-04-07")
    @NotNull
    private LocalDate endDate;
}