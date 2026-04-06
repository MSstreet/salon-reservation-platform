package com.salon.admin.designer.dto;

import com.salon.core.domain.enums.StaffRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "디자이너 등록 요청")
@Getter
@NoArgsConstructor
public class DesignerCreateRequest {

    @Schema(description = "이름", example = "김미용")
    @NotBlank
    private String name;

    @Schema(description = "역할 (DESIGNER | ASSISTANT | STORE_ADMIN)", example = "DESIGNER")
    @NotNull
    private StaffRole role;
}