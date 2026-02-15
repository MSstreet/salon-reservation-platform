package com.salon.api.store;

import com.salon.api.store.dto.StoreCreateRequest;
import com.salon.api.store.dto.StoreResponse;
import com.salon.api.store.dto.StoreUpdateRequest;
import com.salon.domain.entity.Store;
import com.salon.domain.enums.StoreStatus;
import com.salon.domain.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public StoreResponse create(StoreCreateRequest request) {
        Store store = Store.builder()
                .name(request.getName())
                .status(StoreStatus.ACTIVE)
                .timezone(request.getTimezone())
                .build();
        return StoreResponse.from(storeRepository.save(store));
    }

    public StoreResponse getById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found: " + storeId));
        return StoreResponse.from(store);
    }

    public List<StoreResponse> getAll() {
        return storeRepository.findAll().stream()
                .map(StoreResponse::from)
                .toList();
    }

    @Transactional
    public StoreResponse update(Long storeId, StoreUpdateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found: " + storeId));
        store.update(request.getName(), request.getStatus(), request.getTimezone());
        return StoreResponse.from(store);
    }

    @Transactional
    public void delete(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found: " + storeId));
        storeRepository.delete(store);
    }
}
