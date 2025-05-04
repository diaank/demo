package com.example.demo.controller.service;

import com.example.demo.model.Event;
import com.example.demo.service.KafkaProducerService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = KafkaProducerServiceTest.Config.class)
class KafkaProducerServiceTest {

    @Resource
    private KafkaProducerService kafkaProducerService;
    @Resource
    private KafkaTemplate<String, Event> kafkaTemplate;

    @Test
    void shouldRetryUpToThreeTimesOnFailure() {
        // Given
        Event event = new Event(999L, false, "0-0");

        when(kafkaTemplate.send(eq("events"), eq(event)))
                .thenThrow(new RuntimeException("Kafka send failure"));

        // When
        try {
            kafkaProducerService.sendEvent(event);
        } catch (Exception ignored) {
        }

        // Then
        verify(kafkaTemplate, times(3)).send(eq("events"), eq(event));
    }

    @TestConfiguration
    @EnableRetry
    static class Config {

        @Bean
        public KafkaTemplate<String, Event> kafkaTemplate() {
            return mock(KafkaTemplate.class);
        }

        @Bean
        public KafkaProducerService kafkaProducerService(KafkaTemplate<String, Event> kafkaTemplate) {
            return new KafkaProducerService(kafkaTemplate);
        }
    }
}