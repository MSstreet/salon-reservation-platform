package com.salon.api.designer;

import com.salon.api.designer.dto.DesignerResponse;
import com.salon.core.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/designers")
@RequiredArgsConstructor
public class DesignerController {

    private final DesignerService designerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DesignerResponse>>> getDesigners(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(designerService.getDesigners(storeId)));
    }
}
