package com.example.demo.controller.service;

import com.example.demo.model.Event;
import com.example.demo.service.EventService;
import com.example.demo.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private RestClient.RequestHeadersSpec headersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private EventService eventService;

    @Test
    void testCreateEventSuccessfully() {
        Event event = Event.builder()
                .eventId(1L)
                .isLive(true)
                .currentScore("0:0")
                .build();

        Event result = eventService.createEvent(event);

        assertNotNull(result);
        assertEquals(1L, result.getEventId());
        assertEquals("0:0", result.getCurrentScore());
    }

    @Test
    void testCreateEventShouldThrowExceptionIfEventAlreadyExists() {
        // Arrange
        Event event = Event.builder()
                .eventId(1L)
                .isLive(true)
                .currentScore("0:0")
                .build();

        eventService.createEvent(event);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> eventService.createEvent(event));
    }

    @Test
    void testUpdateScoresShouldFetchUpdatedScoresAndSendToKafka() {
        // Arrange
        Event liveEvent = Event.builder()
                .eventId(1L)
                .isLive(true)
                .currentScore("0:0")
                .build();

        Event updatedEvent = Event.builder()
                .eventId(1L)
                .isLive(true)
                .currentScore("1:0")
                .build();

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/api/scores/{eventId}", 1L)).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Event.class)).thenReturn(updatedEvent);

        eventService.createEvent(liveEvent);

        doNothing().when(kafkaProducerService).sendEvent(any(Event.class));

        // Act
        eventService.updateScores();

        // Assert
        verify(kafkaProducerService, atLeastOnce()).sendEvent(updatedEvent);
    }

    @Test
    void testUpdateScoresShouldNotSendEventIfNoLiveEvents() {
        // Act
        eventService.updateScores();

        // Assert
        verify(kafkaProducerService, never()).sendEvent(any(Event.class));
    }

    @Test
    void testUpdateScoresShouldThrowExceptionIfRestClientFails() {
        // Arrange
        Event liveEvent = Event.builder()
                .eventId(1L)
                .isLive(true)
                .currentScore("0:0")
                .build();

        eventService.createEvent(liveEvent);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("http://localhost:8080/api/scores/{eventId}", 1L)).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Event.class)).thenThrow(new RuntimeException("RestClient error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> eventService.updateScores());
        verify(kafkaProducerService, never()).sendEvent(any(Event.class));
    }

}