package com.tastyhouse.webapi.place;

import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.common.ApiResponse;
import com.tastyhouse.webapi.place.request.PlaceNearRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceApiController {

    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Place>>> findAll(@RequestBody PlaceNearRequest placeNearRequest) {
        List<Place> places = placeService.findNearbyPlaces(placeNearRequest.latitude(), placeNearRequest.longitude());
        ApiResponse<List<Place>> response = ApiResponse.success(places);
        return ResponseEntity.ok(response);
    }
}
