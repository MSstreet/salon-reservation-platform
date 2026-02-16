package com.salon.domain.entity;

import com.salon.domain.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "staff_schedule",
    indexes = {
        @Index(name = "idx_schedule_store_date", columnList = "store_id, date"),
        @Index(name = "idx_schedule_staff_date", columnList = "staff_id, date")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_schedule_staff_date", columnNames = {"staff_id", "date"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaffSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ScheduleType type;

    @Builder
    public StaffSchedule(Store store, Staff staff, LocalDate date,
                         LocalTime startTime, LocalTime endTime, ScheduleType type) {
        this.store = store;
        this.staff = staff;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }
}