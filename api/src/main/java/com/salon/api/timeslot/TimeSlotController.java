package com.salon.api.timeslot;

import com.salon.api.timeslot.dto.TimeSlotResponse;
import com.salon.core.domain.enums.SlotStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "TimeSlot", description = "예약 가능 슬롯 조회 API")
@RestController
@RequestMapping("/api/stores/{storeId}/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @Operation(summary = "타임슬롯 목록 조회", description = "특정 날짜·디자이너의 슬롯 목록. status=OPEN 으로 필터하면 예약 가능한 슬롯만 반환.")
    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> getSlots(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", example = "2025-04-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "디자이너(스태프) ID", example = "1")
            @RequestParam Long staffId,
            @Parameter(description = "슬롯 상태 필터 (OPEN | HELD | BOOKED | BLOCKED)", example = "OPEN")
            @RequestParam(required = false) SlotStatus status) {
        return ResponseEntity.ok(timeSlotService.getSlots(storeId, date, staffId, status));
    }
}
