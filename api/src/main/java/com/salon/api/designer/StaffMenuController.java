package com.salon.api.designer;

import com.salon.api.designer.dto.StaffMenuResponse;
import com.salon.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Designer", description = "디자이너(스태프) 조회 API")
@RestController
@RequestMapping("/api/stores/{storeId}/designers/{staffId}/menus")
@RequiredArgsConstructor
public class StaffMenuController {

    private final StaffMenuService staffMenuService;

    @Operation(summary = "디자이너별 메뉴 조회", description = "해당 디자이너가 제공 가능한 시술 메뉴 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffMenuResponse>>> getMenus(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId,
            @Parameter(description = "스태프 ID", example = "1") @PathVariable Long staffId) {
        return ResponseEntity.ok(ApiResponse.success(staffMenuService.getMenus(storeId, staffId)));
    }
}