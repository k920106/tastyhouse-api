package com.tastyhouse.webapi.place.request;

import com.tastyhouse.core.entity.place.Amenity;
import com.tastyhouse.core.entity.place.FoodType;

import java.util.List;

public record LatestPlaceFilterRequest(
    Long stationId,
    List<FoodType> foodTypes,
    List<Amenity> amenities
) {
}
