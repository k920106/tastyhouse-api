package com.tastyhouse.webapi.event.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventDurationResponse {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
