package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LatestPlaceListItem {
    private final Long id;
    private final String name;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
    private final List<String> tags;
    private final LocalDateTime createdAt;
}
