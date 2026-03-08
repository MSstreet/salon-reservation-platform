package com.salon.core.domain.repository;

import com.salon.core.domain.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Optional<Deposit> findByReservationId(Long reservationId);
}