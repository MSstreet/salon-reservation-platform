package com.salon.domain.entity;

import com.salon.domain.enums.ActorType;
import com.salon.domain.enums.EventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservation_history",
    indexes = {
        @Index(name = "idx_history_store_time", columnList = "store_id, occurred_at"),
        @Index(name = "idx_history_res_time", columnList = "reservation_id, occurred_at"),
        @Index(name = "idx_history_type_time", columnList = "event_type, occurred_at")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private EventType eventType;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActorType actorType;

    @Column(length = 100)
    private String actorId;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Builder
    public ReservationHistory(Store store, Reservation reservation, EventType eventType,
                            LocalDateTime occurredAt, ActorType actorType, String actorId,
                            String payloadJson) {
        this.store = store;
        this.reservation = reservation;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.actorType = actorType;
        this.actorId = actorId;
        this.payloadJson = payloadJson;
    }
}