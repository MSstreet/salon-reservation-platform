package com.salon.admin.menu;

import com.salon.admin.menu.dto.MenuCreateRequest;
import com.salon.admin.menu.dto.MenuResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ServiceMenu (Admin)", description = "서비스 메뉴 관리 API (관리자 전용)")
@RestController
@RequestMapping("/admin/stores/{storeId}/menus")
@RequiredArgsConstructor
public class MenuAdminController {

    private final MenuAdminService menuAdminService;

    @Operation(summary = "서비스 메뉴 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAll(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(menuAdminService.getAll(storeId)));
    }

    @Operation(summary = "서비스 메뉴 생성", description = "신규 메뉴는 ACTIVE 상태로 등록됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> create(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Valid @RequestBody MenuCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(menuAdminService.create(storeId, request)));
    }

    @Operation(summary = "메뉴 상태 토글", description = "ACTIVE ↔ INACTIVE 전환")
    @PatchMapping("/{menuId}/toggle")
    public ResponseEntity<ApiResponse<MenuResponse>> toggle(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "메뉴 ID", example = "1") @PathVariable Long menuId) {
        return ResponseEntity.ok(ApiResponse.success(menuAdminService.toggleStatus(storeId, menuId)));
    }
}
