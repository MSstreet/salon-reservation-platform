package com.salon.api.reservation;

import com.salon.api.kafka.ReservationEventPublisher;
import com.salon.core.kafka.KafkaTopics;
import com.salon.core.kafka.ReservationEventPayload;
import com.salon.api.reservation.dto.ReservationResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.Deposit;
import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.entity.ReservationHistory;
import com.salon.core.domain.entity.TimeSlot;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.EventType;
import com.salon.core.domain.enums.ReservationStatus;
import com.salon.core.domain.repository.DepositRepository;
import com.salon.core.domain.repository.ReservationHistoryRepository;
import com.salon.core.domain.repository.ReservationRepository;
import com.salon.core.domain.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationCancelService {

    private static final long FULL_REFUND_HOURS = 24;

    private final ReservationRepository reservationRepository;
    private final DepositRepository depositRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReservationEventPublisher eventPublisher;

    @Transactional
    public ReservationResponse cancel(Long storeId, Long reservationId, String reason) {
        Reservation reservation = reservationRepository.findByIdAndStoreId(reservationId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!isCancelable(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_CANNOT_CANCEL);
        }

        reservation.cancel(reason);
        recoverSlots(reservation);

        Deposit deposit = depositRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPOSIT_NOT_FOUND));

        EventType depositEvent = processDepositOnCancel(deposit, reservation.getStartAt());

        reservationHistoryRepository.save(ReservationHistory.create(
                reservation.getStore(), reservation,
                EventType.RESERVATION_CANCELED,
                LocalDateTime.now(), ActorType.CUSTOMER, null, reason
        ));
        reservationHistoryRepository.save(ReservationHistory.create(
                reservation.getStore(), reservation,
                depositEvent,
                LocalDateTime.now(), ActorType.SYSTEM, null, null
        ));

        eventPublisher.publish(KafkaTopics.RESERVATION_EVENTS,
                ReservationEventPayload.of(reservation, EventType.RESERVATION_CANCELED));
        eventPublisher.publish(KafkaTopics.DEPOSIT_EVENTS,
                ReservationEventPayload.of(reservation, depositEvent));

        return ReservationResponse.from(reservation);
    }

    private boolean isCancelable(ReservationStatus status) {
        return status == ReservationStatus.REQUESTED || status == ReservationStatus.CONFIRMED;
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

    private EventType processDepositOnCancel(Deposit deposit, LocalDateTime reservationStartAt) {
        boolean isFullRefund = LocalDateTime.now()
                .isBefore(reservationStartAt.minusHours(FULL_REFUND_HOURS));

        if (isFullRefund) {
            deposit.refund();
            return EventType.DEPOSIT_REFUNDED;
        } else {
            deposit.forfeit();
            return EventType.DEPOSIT_FORFEITED;
        }
    }
}