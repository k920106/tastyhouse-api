package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import com.tastyhouse.core.entity.place.dto.LatestPlaceItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceRepository {

    List<Place> findNearbyPlaces(BigDecimal latitude, BigDecimal longitude);

    Page<BestPlaceItemDto> findBestPlaces(Pageable pageable);

    Page<LatestPlaceItemDto> findLatestPlaces(Pageable pageable);
}
