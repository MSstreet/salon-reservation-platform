package com.salon.api.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservationCreateRequest(
        @NotNull Long slotId,
        @NotNull Long staffId,
        @NotNull Long productId,
        @NotBlank String customerName,
        @NotBlank String customerPhone
) {
}
