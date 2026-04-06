package com.salon.admin.schedule;

import com.salon.admin.schedule.dto.StaffScheduleCreateRequest;
import com.salon.admin.schedule.dto.StaffScheduleResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "StaffSchedule (Admin)", description = "근무 스케줄 관리 API (관리자 전용)")
@RestController
@RequestMapping("/admin/stores/{storeId}/staff-schedules")
@RequiredArgsConstructor
public class StaffScheduleAdminController {

    private final StaffScheduleAdminService staffScheduleAdminService;

    @Operation(summary = "근무 스케줄 조회", description = "date, staffId 로 필터링 가능")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffScheduleResponse>>> getSchedules(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", example = "2025-04-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "스태프 ID", example = "1")
            @RequestParam(required = false) Long staffId) {
        return ResponseEntity.ok(ApiResponse.success(
                staffScheduleAdminService.getSchedules(storeId, date, staffId)));
    }

    @Operation(
        summary = "근무 스케줄 등록",
        description = "디자이너의 특정 날짜 근무 시간을 등록합니다. 등록 후 타임슬롯 생성 API를 호출하면 슬롯이 생성됩니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<StaffScheduleResponse>> create(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Valid @RequestBody StaffScheduleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(staffScheduleAdminService.create(storeId, request)));
    }
}