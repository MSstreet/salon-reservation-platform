package com.salon.api.scheduler;

import com.salon.api.kafka.ReservationEventPublisher;
import com.salon.core.domain.entity.Deposit;
import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.entity.ReservationHistory;
import com.salon.core.domain.entity.TimeSlot;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.EventType;
import com.salon.core.domain.repository.DepositRepository;
import com.salon.core.domain.repository.ReservationHistoryRepository;
import com.salon.core.domain.repository.TimeSlotRepository;
import com.salon.core.kafka.KafkaTopics;
import com.salon.core.kafka.ReservationEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutScheduler {

    private static final int PAYMENT_TIMEOUT_MINUTES = 10;

    private final DepositRepository depositRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReservationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelExpiredPayments() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES);
        List<Deposit> expiredDeposits = depositRepository.findPendingExpired(expiredBefore);

        if (expiredDeposits.isEmpty()) return;

        log.info("Payment timeout detected: {} deposits", expiredDeposits.size());

        for (Deposit deposit : expiredDeposits) {
            Reservation reservation = deposit.getReservation();
            reservation.cancel("결제 시간 초과로 자동 취소");
            recoverSlots(reservation);

            reservationHistoryRepository.save(ReservationHistory.create(
                    reservation.getStore(), reservation,
                    EventType.RESERVATION_PAYMENT_EXPIRED,
                    LocalDateTime.now(), ActorType.SYSTEM, null, null
            ));

            eventPublisher.publish(KafkaTopics.RESERVATION_EVENTS,
                    ReservationEventPayload.of(reservation, EventType.RESERVATION_PAYMENT_EXPIRED));
        }
    }

    private void recoverSlots(Reservation reservation) {
        List<TimeSlot> slots = timeSlotRepository.findSlotsInRange(
                reservation.getStaff().getId(),
                reservation.getStore().getId(),
                reservation.getStartAt(),
                reservation.getEndAt()
        );
        slots.forEach(TimeSlot::open);
    }
}