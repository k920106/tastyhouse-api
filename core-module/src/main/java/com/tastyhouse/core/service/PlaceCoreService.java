package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.place.*;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import com.tastyhouse.core.entity.place.dto.EditorChoiceDto;
import com.tastyhouse.core.entity.place.dto.LatestPlaceItemDto;
import com.tastyhouse.core.repository.place.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceCoreService {

    private final PlaceRepository placeRepository;
    private final PlaceChoiceRepository placeChoiceRepository;
    private final PlaceStationJpaRepository placeStationJpaRepository;
    private final PlaceFoodTypeCategoryJpaRepository placeFoodTypeCategoryJpaRepository;
    private final PlaceAmenityCategoryJpaRepository placeAmenityCategoryJpaRepository;
    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceBannerImageJpaRepository placeBannerImageJpaRepository;
    private final PlaceImageCategoryJpaRepository placeImageCategoryJpaRepository;
    private final PlacePhotoCategoryImageJpaRepository placePhotoCategoryImageJpaRepository;
    private final PlaceBusinessHourJpaRepository placeBusinessHourJpaRepository;
    private final PlaceBreakTimeJpaRepository placeBreakTimeJpaRepository;
    private final PlaceClosedDayJpaRepository placeClosedDayJpaRepository;
    private final PlaceAmenityJpaRepository placeAmenityJpaRepository;
    private final PlaceOrderMethodJpaRepository placeOrderMethodJpaRepository;

    public List<Place> findNearbyPlaces(Double latitude, Double longitude) {
        BigDecimal lat = BigDecimal.valueOf(latitude);
        BigDecimal lon = BigDecimal.valueOf(longitude);
        return placeRepository.findNearbyPlaces(lat, lon);
    }

    public Page<BestPlaceItemDto> findBestPlaces(int page, int size) {
        return placeRepository.findBestPlaces(PageRequest.of(page, size));
    }

    public Page<LatestPlaceItemDto> findLatestPlaces(int page, int size, Long stationId, List<FoodType> foodTypes, List<Amenity> amenities) {
        return placeRepository.findLatestPlaces(PageRequest.of(page, size), stationId, foodTypes, amenities);
    }

    public List<EditorChoiceDto> findEditorChoices() {
        return placeChoiceRepository.findEditorChoice();
    }

    public Page<EditorChoiceDto> findEditorChoices(int page, int size) {
        return placeChoiceRepository.findEditorChoice(PageRequest.of(page, size));
    }

    public List<PlaceStation> findAllStations() {
        return placeStationJpaRepository.findAllByOrderByStationNameAsc();
    }

    public List<PlaceFoodTypeCategory> findAllFoodTypeCategories() {
        return placeFoodTypeCategoryJpaRepository.findAllByIsActiveTrueOrderBySortAsc();
    }

    public List<PlaceAmenityCategory> findAllAmenityCategories() {
        return placeAmenityCategoryJpaRepository.findAllByIsActiveTrueOrderBySortAsc();
    }

    public Place findPlaceById(Long placeId) {
        return placeJpaRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플레이스입니다. ID: " + placeId));
    }

    public PlaceStation findStationById(Long stationId) {
        return placeStationJpaRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 전철역입니다. ID: " + stationId));
    }

    public PlaceAmenityCategory findPlaceAmenityCategoryById(Long categoryId) {
        return placeAmenityCategoryJpaRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 편의시설 카테고리입니다. ID: " + categoryId));
    }

    public PlacePhotoCategory findPlaceImageCategoryById(Long categoryId) {
        return placeImageCategoryJpaRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지 카테고리입니다. ID: " + categoryId));
    }

    public List<PlaceBusinessHour> findPlaceBusinessHours(Long placeId) {
        return placeBusinessHourJpaRepository.findByPlaceIdOrderByDayType(placeId);
    }

    public List<PlaceBreakTime> findPlaceBreakTimes(Long placeId) {
        return placeBreakTimeJpaRepository.findByPlaceIdOrderByDayType(placeId);
    }

    public List<PlaceClosedDay> findPlaceClosedDays(Long placeId) {
        return placeClosedDayJpaRepository.findByPlaceId(placeId);
    }

    public List<PlaceAmenity> findPlaceAmenities(Long placeId) {
        return placeAmenityJpaRepository.findByPlaceId(placeId);
    }

    public List<PlaceOrderMethod> findPlaceOrderMethods(Long placeId) {
        return placeOrderMethodJpaRepository.findByPlaceId(placeId);
    }

    public List<PlaceBannerImage> findPlaceBannerImages(Long placeId) {
        return placeBannerImageJpaRepository.findByPlaceIdOrderBySortAsc(placeId);
    }

    public List<PlacePhotoCategory> findAllPlacePhotoCategories() {
        return placeImageCategoryJpaRepository.findAll();
    }

    public List<PlacePhotoCategoryImage> findAllPlacePhotoCategoryImages() {
        return placePhotoCategoryImageJpaRepository.findAll(Sort.by("sort").ascending());
    }

    public Page<PlacePhotoCategoryImage> findPlacePhotoCategoryImages(Long placePhotoCategoryId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sort").ascending());

        if (placePhotoCategoryId != null) {
            return placePhotoCategoryImageJpaRepository.findByPlacePhotoCategoryIdOrderBySortAsc(placePhotoCategoryId, pageRequest);
        }
        return placePhotoCategoryImageJpaRepository.findAll(pageRequest);
    }
}
