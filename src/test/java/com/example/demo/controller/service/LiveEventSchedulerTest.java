package com.example.demo.controller.service;

import com.example.demo.service.EventService;
import com.example.demo.service.LiveEventScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LiveEventSchedulerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private LiveEventScheduler liveEventScheduler;

    @Test
    void checkAndUpdateScoresCallsUpdateScores() {
        liveEventScheduler.checkAndUpdateScores();
        verify(eventService, times(1)).updateScores();
    }
}
