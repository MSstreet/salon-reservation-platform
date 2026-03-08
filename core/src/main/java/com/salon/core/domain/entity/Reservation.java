package com.salon.core.domain.entity;

import com.salon.core.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation",
    indexes = {
        @Index(name = "idx_res_store_start", columnList = "store_id, start_at"),
        @Index(name = "idx_res_store_status_start", columnList = "store_id, status, start_at"),
        @Index(name = "idx_res_staff_start", columnList = "staff_id, start_at"),
        @Index(name = "idx_res_customer_phone", columnList = "customer_phone_hash")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private ServiceMenu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private TimeSlot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_customer_id")
    private ReservationCustomer reservationCustomer;

    @Column(nullable = false, length = 50)
    private String customerName;

    @Column(length = 255)
    private String customerPhoneHash;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(length = 500)
    private String cancelReason;

    private Reservation(Store store, Staff staff, ServiceMenu menu, TimeSlot slot,
                        ReservationCustomer reservationCustomer,
                        String customerName, String customerPhoneHash,
                        LocalDateTime startAt, LocalDateTime endAt, ReservationStatus status) {
        this.store = store;
        this.staff = staff;
        this.menu = menu;
        this.slot = slot;
        this.reservationCustomer = reservationCustomer;
        this.customerName = customerName;
        this.customerPhoneHash = customerPhoneHash;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }

    public static Reservation create(Store store, Staff staff, ServiceMenu menu, TimeSlot slot,
                                     ReservationCustomer reservationCustomer,
                                     String customerName, String customerPhoneHash,
                                     LocalDateTime startAt, LocalDateTime endAt, ReservationStatus status) {
        return new Reservation(store, staff, menu, slot, reservationCustomer,
                customerName, customerPhoneHash, startAt, endAt, status);
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel(String reason) {
        this.status = ReservationStatus.CANCELED;
        this.cancelReason = reason;
    }

    public void markNoShow() {
        this.status = ReservationStatus.NO_SHOW;
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }
}