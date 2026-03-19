package com.salon.core.domain.repository;

import com.salon.core.domain.entity.ServiceMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceMenuRepository extends JpaRepository<ServiceMenu, Long> {

    Optional<ServiceMenu> findByIdAndStoreId(Long menuId, Long storeId);
}