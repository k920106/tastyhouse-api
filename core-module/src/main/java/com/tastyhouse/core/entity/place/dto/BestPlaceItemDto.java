package com.tastyhouse.core.entity.place.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tastyhouse.core.entity.place.FoodType;
import lombok.Getter;

import java.util.List;

@Getter
public class BestPlaceItemDto {
    private final Long id;
    private final String name;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
    private final List<FoodType> foodTypes;

    @QueryProjection
    public BestPlaceItemDto(Long id, String name, String stationName, Double rating, String imageUrl, List<FoodType> foodTypes) {
        this.id = id;
        this.name = name;
        this.stationName = stationName;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.foodTypes = foodTypes;
    }
}