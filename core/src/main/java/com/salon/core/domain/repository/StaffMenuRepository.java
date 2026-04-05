package com.salon.core.domain.repository;

import com.salon.core.domain.entity.StaffMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffMenuRepository extends JpaRepository<StaffMenu, Long> {

    List<StaffMenu> findByStaffId(Long staffId);

    boolean existsByStaffIdAndMenuId(Long staffId, Long menuId);

    Optional<StaffMenu> findByStaffIdAndMenuId(Long staffId, Long menuId);
}