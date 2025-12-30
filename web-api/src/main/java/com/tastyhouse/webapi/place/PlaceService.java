package com.tastyhouse.webapi.place;

import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.place.PlaceAmenityCategory;
import com.tastyhouse.core.entity.place.PlaceFoodTypeCategory;
import com.tastyhouse.core.entity.place.PlaceStation;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import com.tastyhouse.core.entity.place.dto.EditorChoiceDto;
import com.tastyhouse.core.entity.place.dto.LatestPlaceItemDto;
import com.tastyhouse.core.entity.product.dto.ProductSimpleDto;
import com.tastyhouse.core.service.PlaceCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.place.response.AmenityListItem;
import com.tastyhouse.webapi.place.response.BestPlaceListItem;
import com.tastyhouse.webapi.place.request.LatestPlaceFilterRequest;
import com.tastyhouse.webapi.place.response.EditorChoiceProductItem;
import com.tastyhouse.webapi.place.response.EditorChoiceResponse;
import com.tastyhouse.webapi.place.response.FoodTypeListItem;
import com.tastyhouse.webapi.place.response.LatestPlaceListItem;
import com.tastyhouse.webapi.place.response.StationListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceCoreService placeCoreService;

    public List<Place> findNearbyPlaces(Double latitude, Double longitude) {
        return placeCoreService.findNearbyPlaces(latitude, longitude);
    }

    public PageResult<BestPlaceListItem> findBestPlaces(PageRequest pageRequest) {
        PlaceCoreService.BestPlacePageResult coreResult = placeCoreService.findBestPlaces(pageRequest.getPage(), pageRequest.getSize());

        List<BestPlaceListItem> bestPlaceListItems = coreResult.getContent().stream().map(this::convertToBestPlaceListItem).toList();

        return new PageResult<>(bestPlaceListItems, coreResult.getTotalElements(), coreResult.getTotalPages(), coreResult.getCurrentPage(), coreResult.getPageSize());
    }

    public PageResult<LatestPlaceListItem> findLatestPlaces(PageRequest pageRequest, LatestPlaceFilterRequest filterRequest) {
        PlaceCoreService.LatestPlacePageResult coreResult = placeCoreService.findLatestPlaces(
                pageRequest.getPage(),
                pageRequest.getSize(),
                filterRequest.stationId(),
                filterRequest.foodTypes(),
                filterRequest.amenities()
        );

        List<LatestPlaceListItem> latestPlaceListItems = coreResult.getContent().stream().map(this::convertToLatestPlaceListItem).toList();

        return new PageResult<>(latestPlaceListItems, coreResult.getTotalElements(), coreResult.getTotalPages(), coreResult.getCurrentPage(), coreResult.getPageSize());
    }

    public List<EditorChoiceResponse> findEditorChoices() {
        List<EditorChoiceDto> editorChoices = placeCoreService.findEditorChoices();

        return editorChoices.stream().map(this::convertToEditorChoiceResponse).toList();
    }

    public List<EditorChoiceResponse> findEditorChoices(PageRequest pageRequest) {
        PlaceCoreService.EditorChoicePageResult coreResult = placeCoreService.findEditorChoices(pageRequest.getPage(), pageRequest.getSize());

        return coreResult.getContent().stream().map(this::convertToEditorChoiceResponse).toList();
    }

    private EditorChoiceResponse convertToEditorChoiceResponse(EditorChoiceDto dto) {
        List<EditorChoiceProductItem> productItems = dto.getProducts() != null ? dto.getProducts().stream().map(this::convertToEditorChoiceProductItem).toList() : new ArrayList<>();

        return EditorChoiceResponse.builder().id(dto.getId()).name(dto.getPlaceName()).imageUrl(dto.getPlaceImageUrl()).title(dto.getTitle()).content(dto.getContent()).products(productItems).build();
    }

    private BestPlaceListItem convertToBestPlaceListItem(BestPlaceItemDto dto) {
        return BestPlaceListItem.builder().id(dto.getId()).name(dto.getPlaceName()).stationName(dto.getStationName()).rating(dto.getRating()).imageUrl(dto.getImageUrl()).foodTypes(dto.getFoodTypes()).build();
    }

    private LatestPlaceListItem convertToLatestPlaceListItem(LatestPlaceItemDto dto) {
        return LatestPlaceListItem.builder().id(dto.getId()).name(dto.getPlaceName()).stationName(dto.getStationName()).rating(dto.getRating()).imageUrl(dto.getImageUrl()).createdAt(dto.getCreatedAt()).reviewCount(dto.getReviewCount()).bookmarkCount(dto.getBookmarkCount()).foodTypes(dto.getFoodTypes()).build();
    }

    private EditorChoiceProductItem convertToEditorChoiceProductItem(ProductSimpleDto dto) {
        return EditorChoiceProductItem.builder().id(dto.getId()).placeName(dto.getPlaceName()).name(dto.getName()).imageUrl(dto.getImageUrl()).originalPrice(dto.getOriginalPrice()).discountPrice(dto.getDiscountPrice()).discountRate(dto.getDiscountRate()).build();
    }

    public List<StationListItem> findAllStations() {
        List<PlaceStation> stations = placeCoreService.findAllStations();
        return stations.stream().map(this::convertToStationListItem).toList();
    }

    public List<FoodTypeListItem> findAllFoodTypes() {
        List<PlaceFoodTypeCategory> categories = placeCoreService.findAllFoodTypeCategories();
        return categories.stream()
                .map(this::convertToFoodTypeListItem)
                .toList();
    }

    public List<AmenityListItem> findAllAmenities() {
        List<PlaceAmenityCategory> categories = placeCoreService.findAllAmenityCategories();
        return categories.stream()
                .map(this::convertToAmenityListItem)
                .toList();
    }

    private StationListItem convertToStationListItem(PlaceStation station) {
        return StationListItem.builder().id(station.getId()).name(station.getStationName()).build();
    }

    private FoodTypeListItem convertToFoodTypeListItem(PlaceFoodTypeCategory category) {
        return FoodTypeListItem.builder()
                .code(category.getFoodType().name())
                .name(category.getDisplayName())
                .imageUrl(category.getImageUrl())
                .build();
    }

    private AmenityListItem convertToAmenityListItem(PlaceAmenityCategory category) {
        return AmenityListItem.builder()
                .code(category.getAmenity().name())
                .name(category.getDisplayName())
                .imageUrlOn(category.getImageUrlOn())
                .imageUrlOff(category.getImageUrlOff())
                .build();
    }
}
