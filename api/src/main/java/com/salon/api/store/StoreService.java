package com.salon.api.store;

import com.salon.api.store.dto.StoreResponse;
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
public class StoreService {

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
        return storeRepository.findByStatus(StoreStatus.ACTIVE).stream()
                .map(StoreResponse::from)
                .toList();
    }
}
