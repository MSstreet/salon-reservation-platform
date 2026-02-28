package com.salon.core.domain.entity;

import com.salon.core.domain.enums.PenaltyType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "penalty",
    indexes = {
        @Index(name = "idx_penalty_store_type_date", columnList = "store_id, type, calculated_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_penalty_reservation", columnNames = {"reservation_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "penalty_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersion policyVersion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private PenaltyType type;

    @Column(nullable = false)
    private Integer ratePercent;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    @Column(columnDefinition = "TEXT")
    private String basisJson;

    private Penalty(Store store, Reservation reservation, PolicyVersion policyVersion,
                    PenaltyType type, Integer ratePercent, Integer amount, String currency,
                    LocalDateTime calculatedAt, String basisJson) {
        this.store = store;
        this.reservation = reservation;
        this.policyVersion = policyVersion;
        this.type = type;
        this.ratePercent = ratePercent;
        this.amount = amount;
        this.currency = currency;
        this.calculatedAt = calculatedAt;
        this.basisJson = basisJson;
    }

    public static Penalty create(Store store, Reservation reservation, PolicyVersion policyVersion,
                                  PenaltyType type, Integer ratePercent, Integer amount, String currency,
                                  LocalDateTime calculatedAt, String basisJson) {
        return new Penalty(store, reservation, policyVersion, type, ratePercent, amount, currency,
                calculatedAt, basisJson);
    }
}
