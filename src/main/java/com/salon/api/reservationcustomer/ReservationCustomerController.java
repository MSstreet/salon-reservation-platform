package com.salon.api.reservationcustomer;

import com.salon.api.reservationcustomer.dto.ReservationCustomerResponse;
import com.salon.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation-customers")
@RequiredArgsConstructor
public class ReservationCustomerController {

    private final ReservationCustomerService reservationCustomerService;

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<ReservationCustomerResponse>> getById(
            @PathVariable Long customerId) {
        ReservationCustomerResponse response = reservationCustomerService.getById(customerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
