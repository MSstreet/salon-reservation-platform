package com.salon.api.reservation;

import com.salon.api.reservation.dto.ReservationCancelRequest;
import com.salon.api.reservation.dto.ReservationCreateRequest;
import com.salon.api.reservation.dto.ReservationResponse;
import com.salon.core.common.response.ApiResponse;
import com.salon.core.domain.enums.ReservationStatus;
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

@Tag(name = "Reservation", description = "예약 API")
@RestController
@RequestMapping("/api/stores/{storeId}/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationCancelService reservationCancelService;

    @Operation(summary = "예약 목록 조회", description = "status, date, staffId 로 필터링 가능")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getList(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "예약 상태 필터", example = "CONFIRMED") @RequestParam(required = false) ReservationStatus status,
            @Parameter(description = "날짜 필터 (yyyy-MM-dd)", example = "2025-04-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "스태프 ID 필터", example = "1") @RequestParam(required = false) Long staffId) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getList(storeId, status, date, staffId)));
    }

    @Operation(
        summary = "예약 생성",
        description = "슬롯을 선택하고 예약을 생성합니다. 예약금(20%) Deposit이 자동 생성되며 상태는 REQUESTED가 됩니다. " +
                      "Idempotency-Key 헤더로 중복 요청을 방지할 수 있습니다 (24h TTL)."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "중복 요청 방지 키 (UUID 권장)", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody ReservationCreateRequest request) {
        ReservationResponse response = reservationService.createReservation(storeId, request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(
        summary = "예약 취소",
        description = "예약 시작 24시간 전 취소 시 예약금 전액 환불, 이후 취소 시 예약금 몰수."
    )
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancel(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long reservationId,
            @Valid @RequestBody ReservationCancelRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationCancelService.cancel(storeId, reservationId, request.getReason())));
    }
}
