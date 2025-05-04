package com.example.demo.service;

import com.example.demo.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducerService {

    private static final String TOPIC = "events";

    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendEvent(Event event) {
        try {
            kafkaTemplate.send(TOPIC, event).get();
            log.info("Message: {} sent to topic {}", event, TOPIC);
        } catch (Exception e) {
            log.warn("Transient failure when sending event. Will retry if attempts remain. Event: {}", event);
            throw new RuntimeException("Kafka topic message send failed", e);
        }
    }

    @Recover
    public void recover(RuntimeException ex, Event event) {
        log.error("Publishing failed after retries. Event: {}", event, ex);
    }
}
