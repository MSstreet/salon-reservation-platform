package com.salon.admin.store;

import com.salon.admin.store.dto.StoreCreateRequest;
import com.salon.admin.store.dto.StoreResponse;
import com.salon.admin.store.dto.StoreUpdateRequest;
import com.salon.core.common.response.ApiResponse;
import com.salon.core.domain.enums.StoreStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
public class StoreAdminController {

    private final StoreAdminService storeAdminService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getAll(
            @RequestParam(required = false) StoreStatus status) {
        return ResponseEntity.ok(ApiResponse.success(storeAdminService.getAll(status)));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> getById(@PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(storeAdminService.getById(storeId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponse>> create(
            @Valid @RequestBody StoreCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(storeAdminService.create(request)));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> update(
            @PathVariable Long storeId,
            @Valid @RequestBody StoreUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(storeAdminService.update(storeId, request)));
    }
}
