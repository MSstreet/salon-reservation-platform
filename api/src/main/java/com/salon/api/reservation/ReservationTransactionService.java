package com.salon.api.reservation;

import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.*;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.EventType;
import com.salon.core.domain.enums.ReservationStatus;
import com.salon.core.domain.repository.ReservationHistoryRepository;
import com.salon.core.domain.repository.ReservationRepository;
import com.salon.core.domain.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationTransactionService {

    private final TimeSlotRepository timeSlotRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    @Transactional
    public Reservation execute(Store store, Staff staff, ServiceProduct product,
                                Long slotId, Long storeId, PolicyVersion policyVersion,
                                String customerName, String customerPhoneHash) {
        TimeSlot slot = timeSlotRepository.findByIdAndStoreId(slotId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SLOT_NOT_FOUND));

        slot.book();

        Reservation reservation = Reservation.create(
                store, staff, product, slot, policyVersion, null,
                customerName, customerPhoneHash,
                slot.getStartAt(), slot.getEndAt(),
                ReservationStatus.CONFIRMED
        );
        reservationRepository.save(reservation);

        ReservationHistory history = ReservationHistory.create(
                store, reservation, EventType.RESERVATION_CREATED,
                LocalDateTime.now(), ActorType.CUSTOMER, null, null
        );
        reservationHistoryRepository.save(history);

        return reservation;
    }
}
