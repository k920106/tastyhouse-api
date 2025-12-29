package com.tastyhouse.core.entity.place.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class LatestPlaceItemDto {
    private final Long id;
    private final String placeName;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
    private final List<String> tags;
    private final LocalDateTime createdAt;
    private final Long reviewCount;
    private final Long bookmarkCount;

    @QueryProjection
    public LatestPlaceItemDto(Long id, String placeName, String stationName, Double rating, String imageUrl, List<String> tags, LocalDateTime createdAt, Long reviewCount, Long bookmarkCount) {
        this.id = id;
        this.placeName = placeName;
        this.stationName = stationName;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.createdAt = createdAt;
        this.reviewCount = reviewCount;
        this.bookmarkCount = bookmarkCount;
    }
}
