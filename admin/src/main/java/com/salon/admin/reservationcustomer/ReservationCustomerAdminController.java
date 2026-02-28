package com.salon.admin.reservationcustomer;

import com.salon.admin.reservationcustomer.dto.ReservationCustomerResponse;
import com.salon.core.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reservation-customers")
@RequiredArgsConstructor
public class ReservationCustomerAdminController {

    private final ReservationCustomerAdminService reservationCustomerAdminService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationCustomerResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(reservationCustomerAdminService.getAll()));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<ReservationCustomerResponse>> getById(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(reservationCustomerAdminService.getById(customerId)));
    }
}
