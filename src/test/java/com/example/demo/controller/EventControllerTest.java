package com.example.demo.controller;

import com.example.demo.model.Event;
import com.example.demo.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private EventController eventController;
    @Mock
    private EventService eventService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    void testCreateEventAndReturnsCreated() throws Exception {
        // Prepare input event
        Event inputEvent = Event.builder()
                .eventId(1L)
                .isLive(true)
                .currentScore("0:0")
                .build();

        when(eventService.createEvent(inputEvent)).thenReturn(inputEvent);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/api/events/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEvent)))  // Convert input to JSON
                .andExpect(status().isCreated())  // Expect HTTP status 201 (Created)
                .andExpect(header().string("Location", "/api/events/1"))  // Check the Location header
                .andExpect(jsonPath("$.eventId").value(1L))  // Check if the event ID is assigned
                .andExpect(jsonPath("$.isLive").value(true))  // Check if the event is live
                .andExpect(jsonPath("$.currentScore").value("0:0"));  // Check the score
    }

    @Test
    void testCreateEventAndReturnsConflict() throws Exception {
        // Prepare input event
        Event inputEvent = Event.builder()
                .eventId(1L)
                .isLive(true)
                .currentScore("0:0")
                .build();

        when(eventService.createEvent(inputEvent))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, String.format("Event with id: '%d' already exists", inputEvent.getEventId())));

        // Perform the POST request and assert the response
        mockMvc.perform(post("/api/events/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEvent)))  // Convert input to JSON
                .andExpect(status().isConflict());  // Expect HTTP status 409 (Created)
    }
}