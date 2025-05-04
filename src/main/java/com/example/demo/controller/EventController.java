package com.example.demo.controller;

import com.example.demo.model.Event;
import com.example.demo.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/events")
@Validated
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/status")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event createdEvent = eventService.createEvent(event);
        URI location = URI.create(String.format("/api/events/%d", event.getEventId()));
        log.info("Event created: {}", createdEvent);
        return ResponseEntity.created(location).body(createdEvent);
    }

}
