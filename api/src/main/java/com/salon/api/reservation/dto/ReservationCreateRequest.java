package com.salon.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "예약 생성 요청")
public record ReservationCreateRequest(
        @Schema(description = "시작 타임슬롯 ID (첫 번째 슬롯)", example = "1")
        @NotNull Long slotId,

        @Schema(description = "디자이너(스태프) ID", example = "1")
        @NotNull Long staffId,

        @Schema(description = "서비스 메뉴 ID", example = "1")
        @NotNull Long menuId,

        @Schema(description = "고객 이름", example = "홍길동")
        @NotBlank String customerName,

        @Schema(description = "고객 전화번호", example = "010-1234-5678")
        @NotBlank String customerPhone
) {
}