package com.salon.api.auth.dto;

import com.salon.core.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Mock 로그인 요청")
public record MockLoginRequest(
        @Schema(description = "사용자 ID", example = "1")
        @NotNull Long userId,

        @Schema(description = "역할 — CUSTOMER: /api/** | STORE_ADMIN: /api/** (storeId 필수) | ADMIN: /admin/**", example = "ADMIN")
        @NotNull UserRole role,

        @Schema(description = "매장 ID (STORE_ADMIN 역할일 때 필수)", example = "1")
        Long storeId
) {
}
