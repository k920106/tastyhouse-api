package com.tastyhouse.webapi.place.response;

import com.tastyhouse.core.entity.place.FoodType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BestPlaceListItem {
    private final Long id;
    private final String name;
    private final String stationName;
    private final Double rating;
    private final String imageUrl;
    private final List<FoodType> foodTypes;
}
