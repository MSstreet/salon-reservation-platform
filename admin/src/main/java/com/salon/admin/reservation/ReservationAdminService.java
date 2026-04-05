package com.salon.admin.reservation;

import com.salon.admin.kafka.ReservationEventPublisher;
import com.salon.admin.reservation.dto.ReservationAdminResponse;
import com.salon.core.kafka.KafkaTopics;
import com.salon.core.kafka.ReservationEventPayload;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.Deposit;
import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.entity.ReservationHistory;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.DepositStatus;
import com.salon.core.domain.enums.EventType;
import com.salon.core.domain.enums.ReservationStatus;
import com.salon.core.domain.repository.DepositRepository;
import com.salon.core.domain.repository.ReservationHistoryRepository;
import com.salon.core.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationRepository reservationRepository;
    private final DepositRepository depositRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReservationEventPublisher eventPublisher;

    @Transactional
    public ReservationAdminResponse complete(Long storeId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndStoreId(reservationId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        reservation.complete();

        reservationHistoryRepository.save(ReservationHistory.create(
                reservation.getStore(), reservation,
                EventType.RESERVATION_COMPLETED,
                LocalDateTime.now(), ActorType.STORE_ADMIN, null, null
        ));

        eventPublisher.publish(KafkaTopics.RESERVATION_EVENTS,
                ReservationEventPayload.of(reservation, EventType.RESERVATION_COMPLETED));

        return ReservationAdminResponse.from(reservation);
    }

    @Transactional
    public ReservationAdminResponse markNoShow(Long storeId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndStoreId(reservationId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        reservation.markNoShow();

        Deposit deposit = depositRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPOSIT_NOT_FOUND));

        if (deposit.getStatus() == DepositStatus.PAID) {
            deposit.forfeit();
            reservationHistoryRepository.save(ReservationHistory.create(
                    reservation.getStore(), reservation,
                    EventType.DEPOSIT_FORFEITED,
                    LocalDateTime.now(), ActorType.STORE_ADMIN, null, null
            ));
        }

        reservationHistoryRepository.save(ReservationHistory.create(
                reservation.getStore(), reservation,
                EventType.RESERVATION_NO_SHOW,
                LocalDateTime.now(), ActorType.STORE_ADMIN, null, null
        ));

        eventPublisher.publish(KafkaTopics.RESERVATION_EVENTS,
                ReservationEventPayload.of(reservation, EventType.RESERVATION_NO_SHOW));

        return ReservationAdminResponse.from(reservation);
    }
}