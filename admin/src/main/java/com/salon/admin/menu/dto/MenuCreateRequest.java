package com.salon.admin.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "서비스 메뉴 생성 요청")
@Getter
@NoArgsConstructor
public class MenuCreateRequest {

    @Schema(description = "메뉴 이름", example = "여성 커트")
    @NotBlank
    private String name;

    @Schema(description = "소요 시간 (분, 30 단위)", example = "60")
    @NotNull @Min(30)
    private Integer durationMin;

    @Schema(description = "가격 (원)", example = "50000")
    @NotNull @Min(0)
    private Integer price;
}