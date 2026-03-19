package com.salon.api.payment.dto;

import com.salon.core.domain.entity.Deposit;
import com.salon.core.domain.enums.DepositStatus;
import com.salon.core.domain.enums.ReservationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponse {

    private final Long depositId;
    private final Long reservationId;
    private final Integer amount;
    private final String currency;
    private final DepositStatus depositStatus;
    private final ReservationStatus reservationStatus;
    private final LocalDateTime paidAt;

    public static PaymentResponse from(Deposit deposit) {
        return new PaymentResponse(
                deposit.getId(),
                deposit.getReservation().getId(),
                deposit.getAmount(),
                deposit.getCurrency(),
                deposit.getStatus(),
                deposit.getReservation().getStatus(),
                deposit.getPaidAt()
        );
    }
}