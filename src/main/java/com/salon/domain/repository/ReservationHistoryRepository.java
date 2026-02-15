package com.salon.domain.repository;

import com.salon.domain.entity.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, UUID> {
}