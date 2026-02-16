package com.salon.domain.repository;

import com.salon.domain.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByStoreId(Long storeId);
}