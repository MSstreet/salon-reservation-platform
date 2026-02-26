package com.salon.domain.repository;

import com.salon.domain.entity.PolicyVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PolicyVersionRepository extends JpaRepository<PolicyVersion, Long> {

    @Query("SELECT pv FROM PolicyVersion pv " +
           "WHERE pv.store.id = :storeId AND pv.effectiveFrom <= :now " +
           "ORDER BY pv.version DESC " +
           "LIMIT 1")
    Optional<PolicyVersion> findLatestEffectivePolicy(
            @Param("storeId") Long storeId,
            @Param("now") LocalDateTime now);
}
