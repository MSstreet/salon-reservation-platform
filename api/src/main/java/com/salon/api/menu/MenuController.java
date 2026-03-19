package com.salon.api.menu;

import com.salon.api.menu.dto.MenuResponse;
import com.salon.core.common.response.ApiResponse;
import com.salon.core.domain.enums.MenuStatus;
import com.salon.core.domain.repository.ServiceMenuRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ServiceMenu", description = "서비스 메뉴 조회 API")
@RestController
@RequestMapping("/api/stores/{storeId}/menus")
@RequiredArgsConstructor
public class MenuController {

    private final ServiceMenuRepository serviceMenuRepository;

    @Operation(summary = "서비스 메뉴 목록 조회", description = "ACTIVE 메뉴만 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAll(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId) {
        List<MenuResponse> menus = serviceMenuRepository.findAllByStoreId(storeId).stream()
                .filter(m -> m.getStatus() == MenuStatus.ACTIVE)
                .map(MenuResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(menus));
    }
}
