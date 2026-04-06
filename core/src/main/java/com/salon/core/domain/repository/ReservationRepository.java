package com.salon.core.domain.repository;

import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStoreId(Long storeId);

    Optional<Reservation> findByIdAndStoreId(Long id, Long storeId);

    List<Reservation> findByStoreIdAndStatus(Long storeId, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.store.id = :storeId " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:date IS NULL OR CAST(r.startAt AS date) = :date) " +
           "AND (:staffId IS NULL OR r.staff.id = :staffId) " +
           "ORDER BY r.startAt ASC")
    List<Reservation> search(@Param("storeId") Long storeId,
                             @Param("status") ReservationStatus status,
                             @Param("date") LocalDate date,
                             @Param("staffId") Long staffId);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED' AND r.startAt < :threshold")
    List<Reservation> findConfirmedNoShows(@Param("threshold") LocalDateTime threshold);
}
