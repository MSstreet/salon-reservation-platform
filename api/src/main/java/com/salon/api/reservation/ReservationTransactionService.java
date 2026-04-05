package com.salon.api.reservation;

import com.salon.api.kafka.ReservationEventPublisher;
import com.salon.core.kafka.KafkaTopics;
import com.salon.core.kafka.ReservationEventPayload;
import com.salon.core.domain.entity.*;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.EventType;
import com.salon.core.domain.enums.ReservationStatus;
import com.salon.core.domain.repository.DepositRepository;
import com.salon.core.domain.repository.ReservationHistoryRepository;
import com.salon.core.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationTransactionService {

    private final ReservationRepository reservationRepository;
    private final DepositRepository depositRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReservationEventPublisher eventPublisher;

    @Transactional
    public Reservation execute(Store store, Staff staff, ServiceMenu menu,
                               List<TimeSlot> slots,
                               String customerName, String customerPhoneHash) {
        slots.forEach(TimeSlot::book);

        TimeSlot firstSlot = slots.get(0);
        LocalDateTime endAt = firstSlot.getStartAt().plusMinutes(menu.getDurationMin());

        Reservation reservation = Reservation.create(
                store, staff, menu, firstSlot, null,
                customerName, customerPhoneHash,
                firstSlot.getStartAt(), endAt,
                ReservationStatus.REQUESTED
        );
        reservationRepository.save(reservation);

        int depositAmount = (int) (menu.getPrice() * 0.2);
        Deposit deposit = Deposit.create(reservation, depositAmount, "KRW");
        depositRepository.save(deposit);

        ReservationHistory createdHistory = ReservationHistory.create(
                store, reservation, EventType.RESERVATION_CREATED,
                LocalDateTime.now(), ActorType.CUSTOMER, null, null
        );
        ReservationHistory depositHistory = ReservationHistory.create(
                store, reservation, EventType.DEPOSIT_CREATED,
                LocalDateTime.now(), ActorType.SYSTEM, null, null
        );
        reservationHistoryRepository.save(createdHistory);
        reservationHistoryRepository.save(depositHistory);

        eventPublisher.publish(KafkaTopics.RESERVATION_EVENTS,
                ReservationEventPayload.of(reservation, EventType.RESERVATION_CREATED));

        return reservation;
    }
}