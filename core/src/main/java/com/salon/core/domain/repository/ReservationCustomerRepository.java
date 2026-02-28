package com.salon.core.domain.repository;

import com.salon.core.domain.entity.ReservationCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationCustomerRepository extends JpaRepository<ReservationCustomer, Long> {

    Optional<ReservationCustomer> findByPhoneHash(String phoneHash);
}
