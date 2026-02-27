package com.salon.domain.entity;

import com.salon.common.exception.BusinessException;
import com.salon.common.exception.ErrorCode;
import com.salon.domain.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_slot",
    indexes = {
        @Index(name = "idx_slot_store_date", columnList = "store_id, date"),
        @Index(name = "idx_slot_staff_start", columnList = "staff_id, start_at"),
        @Index(name = "idx_slot_store_staff_start", columnList = "store_id, staff_id, start_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_slot_staff_start", columnNames = {"staff_id", "start_at"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeSlot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 10)
    private SlotStatus status;

    private LocalDateTime heldUntil;

    private TimeSlot(Store store, Staff staff, LocalDate date,
                     LocalDateTime startAt, LocalDateTime endAt, SlotStatus status) {
        this.store = store;
        this.staff = staff;
        this.date = date;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }

    public static TimeSlot create(Store store, Staff staff, LocalDate date,
                                   LocalDateTime startAt, LocalDateTime endAt, SlotStatus status) {
        return new TimeSlot(store, staff, date, startAt, endAt, status);
    }

    public void book() {
        if (this.status != SlotStatus.OPEN) {
            throw new BusinessException(ErrorCode.SLOT_CONFLICT);
        }
        this.status = SlotStatus.BOOKED;
    }
}