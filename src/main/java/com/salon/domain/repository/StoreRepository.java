package com.salon.domain.repository;

import com.salon.domain.entity.Store;
import com.salon.domain.enums.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByStatus(StoreStatus status);
}
