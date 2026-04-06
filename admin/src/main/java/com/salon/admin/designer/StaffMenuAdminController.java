package com.salon.admin.designer;

import com.salon.admin.designer.dto.StaffMenuAddRequest;
import com.salon.admin.designer.dto.StaffMenuResponse;
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

@Tag(name = "Designer Menu (Admin)", description = "디자이너별 메뉴 매핑 API (관리자 전용)")
@RestController
@RequestMapping("/admin/stores/{storeId}/designers/{staffId}/menus")
@RequiredArgsConstructor
public class StaffMenuAdminController {

    private final StaffMenuAdminService staffMenuAdminService;

    @Operation(summary = "디자이너 메뉴 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffMenuResponse>>> getMenus(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "스태프 ID", example = "1") @PathVariable Long staffId) {
        return ResponseEntity.ok(ApiResponse.success(staffMenuAdminService.getMenus(storeId, staffId)));
    }

    @Operation(summary = "디자이너 메뉴 추가")
    @PostMapping
    public ResponseEntity<ApiResponse<StaffMenuResponse>> addMenu(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "스태프 ID", example = "1") @PathVariable Long staffId,
            @Valid @RequestBody StaffMenuAddRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(staffMenuAdminService.addMenu(storeId, staffId, request.getMenuId())));
    }

    @Operation(summary = "디자이너 메뉴 삭제")
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> removeMenu(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "스태프 ID", example = "1") @PathVariable Long staffId,
            @Parameter(description = "메뉴 ID", example = "1") @PathVariable Long menuId) {
        staffMenuAdminService.removeMenu(storeId, staffId, menuId);
        return ResponseEntity.noContent().build();
    }
}