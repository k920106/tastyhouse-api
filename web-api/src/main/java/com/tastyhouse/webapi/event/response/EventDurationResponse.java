package com.tastyhouse.webapi.event.response;

import java.time.LocalDateTime;

public record EventDurationResponse(
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
