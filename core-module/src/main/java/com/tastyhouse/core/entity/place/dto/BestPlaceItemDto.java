package com.tastyhouse.core.entity.place.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;

@Getter
public class BestPlaceItemDto {
    private final Long id;
    private final String placeName;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
    private final List<String> tags;

    @QueryProjection
    public BestPlaceItemDto(Long id, String placeName, String stationName, Double rating, String imageUrl, List<String> tags) {
        this.id = id;
        this.placeName = placeName;
        this.stationName = stationName;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.tags = tags;
    }
}