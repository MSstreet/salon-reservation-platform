package com.salon.api.payment;

import com.salon.api.payment.dto.PaymentRequest;
import com.salon.api.payment.dto.PaymentResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
        summary = "예약금 결제",
        description = "Deposit을 PAID로 변경하고 예약을 CONFIRMED로 확정합니다."
    )
    @PostMapping("/deposits/{depositId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @Parameter(description = "예약금 ID", example = "1") @PathVariable Long depositId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.pay(depositId, request)));
    }
}