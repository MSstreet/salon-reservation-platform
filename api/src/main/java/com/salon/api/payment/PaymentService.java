package com.salon.api.payment;

import com.salon.api.payment.dto.PaymentRequest;
import com.salon.api.payment.dto.PaymentResponse;
import com.salon.core.common.exception.BusinessException;
import com.salon.core.common.exception.ErrorCode;
import com.salon.core.domain.entity.Deposit;
import com.salon.core.domain.entity.ReservationHistory;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.DepositStatus;
import com.salon.core.domain.enums.EventType;
import com.salon.core.domain.repository.DepositRepository;
import com.salon.core.domain.repository.ReservationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final DepositRepository depositRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    @Transactional
    public PaymentResponse pay(Long depositId, PaymentRequest request) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPOSIT_NOT_FOUND));

        if (deposit.getStatus() != DepositStatus.PENDING) {
            throw new BusinessException(ErrorCode.DEPOSIT_INVALID_STATUS);
        }

        deposit.pay();
        deposit.getReservation().confirm();

        reservationHistoryRepository.save(ReservationHistory.create(
                deposit.getReservation().getStore(),
                deposit.getReservation(),
                EventType.DEPOSIT_PAID,
                LocalDateTime.now(), ActorType.CUSTOMER, null, null
        ));
        reservationHistoryRepository.save(ReservationHistory.create(
                deposit.getReservation().getStore(),
                deposit.getReservation(),
                EventType.RESERVATION_CONFIRMED,
                LocalDateTime.now(), ActorType.SYSTEM, null, null
        ));

        return PaymentResponse.from(deposit);
    }
}