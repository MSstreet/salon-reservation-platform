package com.salon.admin.timeslot;

import com.salon.admin.timeslot.dto.TimeSlotGenerateRequest;
import com.salon.admin.timeslot.dto.TimeSlotGenerateResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TimeSlot (Admin)", description = "타임슬롯 관리 API (관리자 전용)")
@RestController
@RequestMapping("/admin/stores/{storeId}/time-slots")
@RequiredArgsConstructor
public class TimeSlotAdminController {

    private final TimeSlotAdminService timeSlotAdminService;

    @Operation(
        summary = "타임슬롯 일괄 생성",
        description = "등록된 근무 스케줄(WORK)을 기반으로 30분 단위 슬롯을 자동 생성합니다. " +
                      "이미 존재하는 슬롯은 건너뛰며(멱등), skippedCount로 확인할 수 있습니다."
    )
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<TimeSlotGenerateResponse>> generate(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Valid @RequestBody TimeSlotGenerateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(timeSlotAdminService.generate(storeId, request)));
    }
}