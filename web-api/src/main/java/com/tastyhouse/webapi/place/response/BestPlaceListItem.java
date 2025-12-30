package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BestPlaceListItem {
    private final Long id;
    private final String name;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
}
