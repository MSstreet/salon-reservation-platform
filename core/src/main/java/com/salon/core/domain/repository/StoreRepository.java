package com.salon.core.domain.repository;

import com.salon.core.domain.entity.Store;
import com.salon.core.domain.enums.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByStatus(StoreStatus status);
}
