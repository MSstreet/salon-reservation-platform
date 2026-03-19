package com.salon.api.scheduler;

import com.salon.core.domain.entity.Deposit;
import com.salon.core.domain.entity.Reservation;
import com.salon.core.domain.entity.ReservationHistory;
import com.salon.core.domain.enums.ActorType;
import com.salon.core.domain.enums.EventType;
import com.salon.core.domain.repository.DepositRepository;
import com.salon.core.domain.repository.ReservationHistoryRepository;
import com.salon.core.domain.repository.ReservationRepository;
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
public class NoShowScheduler {

    private static final int GRACE_PERIOD_MINUTES = 10;

    private final ReservationRepository reservationRepository;
    private final DepositRepository depositRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    @Scheduled(fixedDelay = 60_000) // 1분마다 실행
    @Transactional
    public void detectNoShows() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(GRACE_PERIOD_MINUTES);
        List<Reservation> noShows = reservationRepository.findConfirmedNoShows(threshold);

        if (noShows.isEmpty()) return;

        log.info("No-show detected: {} reservations", noShows.size());

        for (Reservation reservation : noShows) {
            reservation.markNoShow();

            depositRepository.findByReservationId(reservation.getId())
                    .ifPresent(Deposit::forfeit);

            reservationHistoryRepository.save(ReservationHistory.create(
                    reservation.getStore(), reservation,
                    EventType.RESERVATION_NO_SHOW,
                    LocalDateTime.now(), ActorType.SYSTEM, null, null
            ));
            reservationHistoryRepository.save(ReservationHistory.create(
                    reservation.getStore(), reservation,
                    EventType.DEPOSIT_FORFEITED,
                    LocalDateTime.now(), ActorType.SYSTEM, null, null
            ));
        }
    }
}