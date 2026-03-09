package com.salon.admin.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "매장 생성 요청")
@Getter
@NoArgsConstructor
public class StoreCreateRequest {

    @Schema(description = "매장 이름", example = "강남 헤어샵")
    @NotBlank
    private String name;

    @Schema(description = "타임존 (IANA)", example = "Asia/Seoul")
    @NotBlank
    private String timezone;
}
