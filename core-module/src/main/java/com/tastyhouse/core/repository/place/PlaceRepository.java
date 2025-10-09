package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.Place;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceRepository {

    List<Place> findNearbyPlaces(BigDecimal latitude, BigDecimal longitude);
}
