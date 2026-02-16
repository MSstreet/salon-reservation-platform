package com.salon.domain.repository;

import com.salon.domain.entity.PolicyVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyVersionRepository extends JpaRepository<PolicyVersion, Long> {
    List<PolicyVersion> findByStoreId(Long storeId);
}
