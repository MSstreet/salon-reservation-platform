package com.salon.core.common.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ApiError {

    private final String code;
    private final String message;
    private final List<String> details;
}
