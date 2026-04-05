package com.salon.api.kafka;

import com.salon.core.kafka.KafkaTopics;
import com.salon.core.kafka.ReservationEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @KafkaListener(topics = KafkaTopics.RESERVATION_EVENTS, groupId = "salon-notification-group")
    public void handleReservationEvent(ReservationEventPayload payload) {
        log.info("[Notification] reservationId={}, eventType={}, customer={}, startAt={}",
                payload.getReservationId(), payload.getEventType(),
                payload.getCustomerName(), payload.getStartAt());
        // TODO: 알림 발송 (SMS, Push 등) 연동
    }

    @KafkaListener(topics = KafkaTopics.DEPOSIT_EVENTS, groupId = "salon-notification-group")
    public void handleDepositEvent(ReservationEventPayload payload) {
        log.info("[Notification] deposit event - reservationId={}, eventType={}",
                payload.getReservationId(), payload.getEventType());
        // TODO: 결제 관련 알림 연동
    }
}
