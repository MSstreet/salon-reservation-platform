package com.salon.core.domain.repository;

import com.salon.core.domain.entity.Staff;
import com.salon.core.domain.enums.StaffRole;
import com.salon.core.domain.enums.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByStoreId(Long storeId);

    List<Staff> findByStoreIdAndRoleAndStatus(Long storeId, StaffRole role, StaffStatus status);
}
