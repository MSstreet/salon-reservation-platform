package com.salon.api.store;

import com.salon.api.store.dto.StoreResponse;
import com.salon.core.common.response.ApiResponse;
import com.salon.core.domain.enums.StoreStatus;
import com.salon.core.security.StoreAccessValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store (API)", description = "매장 조회 API")
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final StoreAccessValidator storeAccessValidator;

    @Operation(summary = "매장 단건 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getById(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId) {
        storeAccessValidator.validateAccess(storeId);
        return ResponseEntity.ok(storeService.getById(storeId));
    }

    @Operation(summary = "매장 목록 조회", description = "status 파라미터로 필터링 가능 (ACTIVE | INACTIVE)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getAll(
            @Parameter(description = "매장 상태 필터 (생략 시 전체)", example = "ACTIVE")
            @RequestParam(required = false) StoreStatus status) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getAll(status)));
    }
}
