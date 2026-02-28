package com.salon.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_version",
    indexes = {
        @Index(name = "idx_policy_store_effective", columnList = "store_id, effective_from")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_policy_store_version", columnNames = {"store_id", "version"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PolicyVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_version_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private Integer version;

    private LocalDateTime effectiveFrom;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rulesJson;

    @Column(length = 100)
    private String createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PolicyVersion(Store store, Integer version, LocalDateTime effectiveFrom,
                          String rulesJson, String createdBy) {
        this.store = store;
        this.version = version;
        this.effectiveFrom = effectiveFrom;
        this.rulesJson = rulesJson;
        this.createdBy = createdBy;
    }

    public static PolicyVersion create(Store store, Integer version, LocalDateTime effectiveFrom,
                                        String rulesJson, String createdBy) {
        return new PolicyVersion(store, version, effectiveFrom, rulesJson, createdBy);
    }
}
