package com.salon.core.domain.repository;

import com.salon.core.domain.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Optional<Deposit> findByReservationId(Long reservationId);

    @Query("SELECT d FROM Deposit d WHERE d.status = 'PENDING' AND d.createdAt < :expiredBefore")
    List<Deposit> findPendingExpired(@Param("expiredBefore") LocalDateTime expiredBefore);
}