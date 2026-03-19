package com.salon.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "결제 요청")
@Getter
@NoArgsConstructor
public class PaymentRequest {

    @Schema(description = "결제 수단 (CARD | KAKAO_PAY | NAVER_PAY)", example = "CARD")
    @NotBlank
    private String paymentMethod;
}