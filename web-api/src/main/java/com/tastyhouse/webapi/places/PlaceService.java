package com.tastyhouse.webapi.places;

import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.service.PlaceCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceCoreService placeCoreService;

    public List<Place> findNearbyPlaces(Double latitude, Double longitude) {
        return placeCoreService.findNearbyPlaces(latitude, longitude);
    }
}
