package com.salon.admin.designer;

import com.salon.admin.designer.dto.DesignerCreateRequest;
import com.salon.admin.designer.dto.DesignerResponse;
import com.salon.admin.designer.dto.DesignerStatusRequest;
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

@Tag(name = "Designer (Admin)", description = "디자이너 관리 API (관리자 전용)")
@RestController
@RequestMapping("/admin/stores/{storeId}/designers")
@RequiredArgsConstructor
public class DesignerAdminController {

    private final DesignerAdminService designerAdminService;

    @Operation(summary = "디자이너 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DesignerResponse>>> getAll(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(designerAdminService.getAll(storeId)));
    }

    @Operation(summary = "디자이너 등록", description = "신규 등록 시 상태는 ACTIVE로 자동 설정됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<DesignerResponse>> create(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Valid @RequestBody DesignerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(designerAdminService.create(storeId, request)));
    }

    @Operation(summary = "디자이너 상태 변경", description = "ACTIVE ↔ INACTIVE 전환")
    @PatchMapping("/{staffId}/status")
    public ResponseEntity<ApiResponse<DesignerResponse>> updateStatus(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "스태프 ID", example = "1") @PathVariable Long staffId,
            @Valid @RequestBody DesignerStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                designerAdminService.updateStatus(storeId, staffId, request.getStatus())));
    }
}