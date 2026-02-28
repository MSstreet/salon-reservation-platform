package com.salon.core.domain.repository;

import com.salon.core.domain.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByStoreId(Long storeId);

    List<TimeSlot> findByStoreIdAndDate(Long storeId, LocalDate date);

    List<TimeSlot> findByStoreIdAndDateAndStaffId(Long storeId, LocalDate date, Long staffId);

    Optional<TimeSlot> findByIdAndStoreId(Long id, Long storeId);
}
