package com.salon.api.store;

import com.salon.api.store.dto.StoreResponse;
import com.salon.security.StoreAccessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final StoreAccessValidator storeAccessValidator;

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getById(@PathVariable Long storeId) {
        storeAccessValidator.validateAccess(storeId);
        return ResponseEntity.ok(storeService.getById(storeId));
    }

    @GetMapping
    public ResponseEntity<List<StoreResponse>> getAll() {
        return ResponseEntity.ok(storeService.getAll());
    }


}
