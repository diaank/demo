package com.example.demo.controller;


import com.example.demo.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class MockExternalEventScoreController {

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventScores(@PathVariable Long eventId) {
        Event updatedEvent = Event.builder().eventId(eventId).isLive(true)
                .currentScore(new Random().nextInt(11) + ":" + new Random().nextInt(11)).build();
        return ResponseEntity.ok(updatedEvent);
    }
}
