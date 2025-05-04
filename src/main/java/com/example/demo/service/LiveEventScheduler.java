package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiveEventScheduler {

    private final EventService eventService;

    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void checkAndUpdateScores() {
        eventService.updateScores();
    }
}
