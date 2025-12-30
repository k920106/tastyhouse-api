package com.tastyhouse.core.entity.place.dto;

import com.tastyhouse.core.entity.place.FoodType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LatestPlaceItemDto {
    private final Long id;
    private final String placeName;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private final Long reviewCount;
    private final Long bookmarkCount;
    private final List<FoodType> foodTypes;
}
