package com.example.demo.service;

import com.example.demo.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final KafkaProducerService kafkaProducerService;
    private final RestClient restClient;
    private final List<Event> eventList = new ArrayList<>();

    public Event createEvent(Event event) {
        eventList.stream().filter(checkedEvent -> checkedEvent.getEventId().equals(event.getEventId())).findAny()
                .ifPresent((checkedEvent) -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Event with id: '%d' already exists", checkedEvent.getEventId()));
                });
        eventList.add(event);
        return event;
    }

    public void updateScores() {
        eventList.stream().filter(checkedEvent -> checkedEvent.getIsLive()).forEach(liveEvent ->
                {
                    Event updatedEvent =
                            restClient.get()
                                    .uri("/api/scores/{eventId}", liveEvent.getEventId()).retrieve().body(Event.class);
                    log.info("New score fetched for event with id: {}", updatedEvent.getEventId());
                    kafkaProducerService.sendEvent(updatedEvent);
                }
        );
    }
}
