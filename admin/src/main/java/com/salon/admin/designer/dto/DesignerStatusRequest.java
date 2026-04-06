package com.salon.admin.designer.dto;

import com.salon.core.domain.enums.StaffStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "디자이너 상태 변경 요청")
@Getter
@NoArgsConstructor
public class DesignerStatusRequest {

    @Schema(description = "변경할 상태 (ACTIVE | INACTIVE)", example = "INACTIVE")
    @NotNull
    private StaffStatus status;
}