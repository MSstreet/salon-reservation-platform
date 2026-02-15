package com.salon.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ApiError error;
    private final Map<String, Object> meta;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(buildMeta())
                .build();
    }

    private static Map<String, Object> buildMeta() {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("requestId", null);
        meta.put("timestamp", Instant.now().toString());
        return meta;
    }
}
