package com.salon.admin.reservation;

import com.salon.admin.reservation.dto.ReservationAdminResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation (Admin)", description = "예약 상태 관리 API (관리자 전용)")
@RestController
@RequestMapping("/admin/stores/{storeId}/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationAdminService reservationAdminService;

    @Operation(summary = "예약 완료 처리", description = "CONFIRMED 상태의 예약을 COMPLETED로 변경합니다.")
    @PatchMapping("/{reservationId}/complete")
    public ResponseEntity<ApiResponse<ReservationAdminResponse>> complete(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationAdminService.complete(storeId, reservationId)));
    }

    @Operation(summary = "노쇼 처리", description = "CONFIRMED 상태의 예약을 NO_SHOW로 변경하고 예약금을 몰수합니다.")
    @PatchMapping("/{reservationId}/no-show")
    public ResponseEntity<ApiResponse<ReservationAdminResponse>> noShow(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationAdminService.markNoShow(storeId, reservationId)));
    }
}