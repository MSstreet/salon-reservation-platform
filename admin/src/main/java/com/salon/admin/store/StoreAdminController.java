package com.salon.admin.store;

import com.salon.admin.store.dto.StoreCreateRequest;
import com.salon.admin.store.dto.StoreResponse;
import com.salon.admin.store.dto.StoreUpdateRequest;
import com.salon.core.common.response.ApiResponse;
import com.salon.core.domain.enums.StoreStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store (Admin)", description = "매장 관리 API (관리자 전용)")
@RestController
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
public class StoreAdminController {

    private final StoreAdminService storeAdminService;

    @Operation(summary = "매장 목록 조회", description = "status로 필터링 가능 (ACTIVE | INACTIVE)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getAll(
            @Parameter(description = "매장 상태 필터 (생략 시 전체)", example = "ACTIVE")
            @RequestParam(required = false) StoreStatus status) {
        return ResponseEntity.ok(ApiResponse.success(storeAdminService.getAll(status)));
    }

    @Operation(summary = "매장 단건 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> getById(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(storeAdminService.getById(storeId)));
    }

    @Operation(summary = "매장 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponse>> create(
            @Valid @RequestBody StoreCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(storeAdminService.create(request)));
    }

    @Operation(summary = "매장 수정")
    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> update(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Valid @RequestBody StoreUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(storeAdminService.update(storeId, request)));
    }
}
