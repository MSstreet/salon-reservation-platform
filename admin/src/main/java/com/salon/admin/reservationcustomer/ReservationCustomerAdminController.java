package com.salon.admin.reservationcustomer;

import com.salon.admin.reservationcustomer.dto.ReservationCustomerResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ReservationCustomer (Admin)", description = "예약 고객 관리 API (관리자 전용)")
@RestController
@RequestMapping("/admin/reservation-customers")
@RequiredArgsConstructor
public class ReservationCustomerAdminController {

    private final ReservationCustomerAdminService reservationCustomerAdminService;

    @Operation(summary = "예약 고객 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationCustomerResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(reservationCustomerAdminService.getAll()));
    }

    @Operation(summary = "예약 고객 단건 조회")
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<ReservationCustomerResponse>> getById(
            @Parameter(description = "고객 ID", example = "1") @PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(reservationCustomerAdminService.getById(customerId)));
    }
}