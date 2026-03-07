package com.salon.api.auth.dto;

import com.salon.core.domain.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record MockLoginRequest(
        @NotNull Long userId,
        @NotNull UserRole role,
        Long storeId
) {
}
