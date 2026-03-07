package com.salon.core.domain.repository;

import com.salon.core.domain.entity.ServiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceProductRepository extends JpaRepository<ServiceProduct, Long> {
    List<ServiceProduct> findByStoreId(Long storeId);
}
