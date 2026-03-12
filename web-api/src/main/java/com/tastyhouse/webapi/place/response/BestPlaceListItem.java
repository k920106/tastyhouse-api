package com.tastyhouse.webapi.place.response;

import com.tastyhouse.core.entity.place.FoodType;

import java.util.List;

public record BestPlaceListItem(
        Long id,
        String name,
        String stationName,
        Double rating,
        String imageUrl,
        List<FoodType> foodTypes
) {
}
