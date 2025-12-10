package com.tastyhouse.webapi.place;

import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import com.tastyhouse.core.service.PlaceCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.place.response.BestPlaceListItem;
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

    public PageResult<BestPlaceListItem> findBestPlaces(PageRequest pageRequest) {
        PlaceCoreService.BestPlacePageResult coreResult = placeCoreService.findBestPlaces(
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<BestPlaceListItem> bestPlaceListItems = coreResult.getContent().stream()
            .map(this::convertToBestPlaceListItem)
            .toList();

        return new PageResult<>(
            bestPlaceListItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
    }

    private BestPlaceListItem convertToBestPlaceListItem(BestPlaceItemDto dto) {
        return BestPlaceListItem.builder()
            .id(dto.getId())
            .placeName(dto.getPlaceName())
            .stationName(dto.getStationName())
            .rating(dto.getRating())
            .imageUrl(dto.getImageUrl())
            .tags(dto.getTags())
            .build();
    }
}
