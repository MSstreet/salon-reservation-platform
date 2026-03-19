package com.salon.api.designer;

import com.salon.api.designer.dto.DesignerResponse;
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
@RequestMapping("/api/stores/{storeId}/designers")
@RequiredArgsConstructor
public class DesignerController {

    private final DesignerService designerService;

    @Operation(summary = "매장 디자이너 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DesignerResponse>>> getDesigners(
            @Parameter(description = "매장 ID", example = "1") @PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(designerService.getDesigners(storeId)));
    }
}
