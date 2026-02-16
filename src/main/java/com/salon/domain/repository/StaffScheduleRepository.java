package com.salon.domain.repository;

import com.salon.domain.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, Long> {
    List<StaffSchedule> findByStoreId(Long storeId);
}