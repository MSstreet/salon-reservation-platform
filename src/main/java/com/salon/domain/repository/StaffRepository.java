package com.salon.domain.repository;

import com.salon.domain.entity.Staff;
import com.salon.domain.enums.StaffRole;
import com.salon.domain.enums.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByStoreId(Long storeId);

    List<Staff> findByStoreIdAndRoleAndStatus(Long storeId, StaffRole role, StaffStatus status);
}