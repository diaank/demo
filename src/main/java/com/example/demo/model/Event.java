package com.example.demo.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @NonNull
    private Long eventId;
    private Boolean isLive;
    private String currentScore;
}
