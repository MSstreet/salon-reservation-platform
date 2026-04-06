package com.salon.admin.store;

import com.salon.admin.store.dto.StoreCreateRequest;
import com.salon.admin.store.dto.StoreResponse;
import com.salon.admin.store.dto.StoreUpdateRequest;
import com.salon.core.domain.entity.Store;
import com.salon.core.domain.enums.StoreStatus;
import com.salon.core.domain.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreAdminService {

    private final StoreRepository storeRepository;

    public StoreResponse getById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found: " + storeId));
        return StoreResponse.from(store);
    }

    public List<StoreResponse> getAll(StoreStatus status) {
        if (status != null) {
            return storeRepository.findByStatus(status).stream()
                    .map(StoreResponse::from)
                    .toList();
        }
        return storeRepository.findAll().stream()
                .map(StoreResponse::from)
                .toList();
    }

    @Transactional
    public StoreResponse create(StoreCreateRequest request) {
        Store store = Store.create(request.getName(), StoreStatus.ACTIVE, request.getTimezone(), request.getAddress());
        return StoreResponse.from(storeRepository.save(store));
    }

    @Transactional
    public StoreResponse update(Long storeId, StoreUpdateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found: " + storeId));
        store.update(request.getName(), request.getStatus(), request.getTimezone(), request.getAddress());
        return StoreResponse.from(store);
    }
}
