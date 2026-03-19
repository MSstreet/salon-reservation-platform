package com.salon.core.domain.repository;

import com.salon.core.domain.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, Long> {
    List<StaffSchedule> findByStoreId(Long storeId);

    Optional<StaffSchedule> findByStaffIdAndDate(Long staffId, LocalDate date);

    @Query("SELECT s FROM StaffSchedule s WHERE s.staff.id = :staffId " +
           "AND s.date >= :startDate AND s.date <= :endDate")
    List<StaffSchedule> findByStaffIdAndDateRange(@Param("staffId") Long staffId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
}
