package com.salon.core.domain.repository;

import com.salon.core.domain.entity.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, UUID> {
}
