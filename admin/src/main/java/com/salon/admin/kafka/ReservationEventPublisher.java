package com.salon.admin.kafka;

import com.salon.core.kafka.ReservationEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, ReservationEventPayload payload) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(topic, payload);
                }
            });
        } else {
            send(topic, payload);
        }
    }

    private void send(String topic, ReservationEventPayload payload) {
        kafkaTemplate.send(topic, String.valueOf(payload.getReservationId()), payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka publish failed: topic={}, reservationId={}, eventType={}",
                                topic, payload.getReservationId(), payload.getEventType(), ex);
                    } else {
                        log.debug("Kafka publish success: topic={}, reservationId={}, eventType={}",
                                topic, payload.getReservationId(), payload.getEventType());
                    }
                });
    }
}