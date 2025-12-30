package com.tastyhouse.core.entity.place.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BestPlaceItemDto {
    private final Long id;
    private final String placeName;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;

    @QueryProjection
    public BestPlaceItemDto(Long id, String placeName, String stationName, Double rating, String imageUrl) {
        this.id = id;
        this.placeName = placeName;
        this.stationName = stationName;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }
}