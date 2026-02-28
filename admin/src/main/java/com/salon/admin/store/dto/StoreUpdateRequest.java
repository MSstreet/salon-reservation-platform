package com.salon.admin.store.dto;

import com.salon.core.domain.enums.StoreStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreUpdateRequest {

    @NotBlank
    private String name;

    @NotNull
    private StoreStatus status;

    @NotBlank
    private String timezone;
}
