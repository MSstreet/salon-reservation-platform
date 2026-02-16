package com.salon.api.store.dto;

import com.salon.domain.enums.StoreStatus;
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
