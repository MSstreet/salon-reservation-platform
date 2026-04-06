package com.salon.admin.store.dto;

import com.salon.core.domain.enums.StoreStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "매장 수정 요청")
@Getter
@NoArgsConstructor
public class StoreUpdateRequest {

    @Schema(description = "매장 이름", example = "강남 헤어샵 (리뉴얼)")
    @NotBlank
    private String name;

    @Schema(description = "매장 상태 (ACTIVE | INACTIVE)", example = "ACTIVE")
    @NotNull
    private StoreStatus status;

    @Schema(description = "타임존 (IANA)", example = "Asia/Seoul")
    @NotBlank
    private String timezone;

    @Schema(description = "매장 주소", example = "서울시 강남구 테헤란로 123")
    private String address;
}
