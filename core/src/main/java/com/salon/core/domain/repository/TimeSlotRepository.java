package com.salon.core.domain.repository;

import com.salon.core.domain.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByStoreId(Long storeId);

    List<TimeSlot> findByStoreIdAndDate(Long storeId, LocalDate date);

    List<TimeSlot> findByStoreIdAndDateAndStaffId(Long storeId, LocalDate date, Long staffId);

    Optional<TimeSlot> findByIdAndStoreId(Long id, Long storeId);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.staff.id = :staffId " +
           "AND ts.store.id = :storeId " +
           "AND ts.startAt >= :startAt " +
           "AND ts.startAt < :endAt " +
           "ORDER BY ts.startAt ASC")
    List<TimeSlot> findSlotsInRange(@Param("staffId") Long staffId,
                                    @Param("storeId") Long storeId,
                                    @Param("startAt") LocalDateTime startAt,
                                    @Param("endAt") LocalDateTime endAt);
}
