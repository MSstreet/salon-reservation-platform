package com.salon.api.reservation;

import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.*;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.DepositStatus;
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

@Service
@RequiredArgsConstructor
public class ReservationTransactionService {

    private final TimeSlotRepository timeSlotRepository;
    private final ReservationRepository reservationRepository;
    private final DepositRepository depositRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    @Transactional
    public Reservation execute(Store store, Staff staff, ServiceMenu menu,
                               Long slotId, Long storeId,
                               String customerName, String customerPhoneHash) {
        TimeSlot slot = timeSlotRepository.findByIdAndStoreId(slotId, storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SLOT_NOT_FOUND));

        slot.book();

        Reservation reservation = Reservation.create(
                store, staff, menu, slot, null,
                customerName, customerPhoneHash,
                slot.getStartAt(), slot.getEndAt(),
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

        return reservation;
    }
}