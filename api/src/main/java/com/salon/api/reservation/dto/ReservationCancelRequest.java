package com.salon.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "예약 취소 요청")
@Getter
@NoArgsConstructor
public class ReservationCancelRequest {

    @Schema(description = "취소 사유 (선택)", example = "개인 사정으로 인한 취소")
    private String reason;
}