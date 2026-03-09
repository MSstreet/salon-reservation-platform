package com.salon.api.reservationcustomer;

import com.salon.api.reservationcustomer.dto.ReservationCustomerResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ReservationCustomer (API)", description = "예약 고객 조회 API")
@RestController
@RequestMapping("/api/reservation-customers")
@RequiredArgsConstructor
public class ReservationCustomerController {

    private final ReservationCustomerService reservationCustomerService;

    @Operation(summary = "예약 고객 단건 조회")
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<ReservationCustomerResponse>> getById(
            @Parameter(description = "고객 ID", example = "1") @PathVariable Long customerId) {
        ReservationCustomerResponse response = reservationCustomerService.getById(customerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
