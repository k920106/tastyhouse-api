package com.tastyhouse.core.entity.place.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MyBookmarkedPlaceItemDto {
    private final Long placeId;
    private final Long bookmarkId;
    private final String placeName;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
    private final Boolean isBookmarked;

    @QueryProjection
    public MyBookmarkedPlaceItemDto(Long placeId, Long bookmarkId, String placeName, String stationName, Double rating, String imageUrl, Boolean isBookmarked) {
        this.placeId = placeId;
        this.bookmarkId = bookmarkId;
        this.placeName = placeName;
        this.stationName = stationName;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.isBookmarked = isBookmarked;
    }
}
