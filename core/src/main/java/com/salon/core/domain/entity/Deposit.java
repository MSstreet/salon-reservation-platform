package com.salon.core.domain.entity;

import com.salon.core.domain.enums.DepositStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "deposit",
    indexes = {
        @Index(name = "idx_deposit_reservation", columnList = "reservation_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_deposit_reservation", columnNames = {"reservation_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private DepositStatus status;

    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private LocalDateTime forfeitedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Deposit(Reservation reservation, Integer amount, String currency) {
        this.reservation = reservation;
        this.amount = amount;
        this.currency = currency;
        this.status = DepositStatus.PENDING;
    }

    public static Deposit create(Reservation reservation, Integer amount, String currency) {
        return new Deposit(reservation, amount, currency);
    }

    public void pay() {
        this.status = DepositStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void refund() {
        this.status = DepositStatus.REFUNDED;
        this.refundedAt = LocalDateTime.now();
    }

    public void forfeit() {
        this.status = DepositStatus.FORFEITED;
        this.forfeitedAt = LocalDateTime.now();
    }
}