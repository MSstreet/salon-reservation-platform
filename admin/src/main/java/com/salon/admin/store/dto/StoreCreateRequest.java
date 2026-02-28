package com.salon.admin.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String timezone;
}
