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

    public List<Place> findNearbyPlaces(Double latitude, Double longitude) {
        BigDecimal lat = BigDecimal.valueOf(latitude);
        BigDecimal lon = BigDecimal.valueOf(longitude);
        return placeRepository.findNearbyPlaces(lat, lon);
    }

    public BestPlacePageResult findBestPlaces(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BestPlaceItemDto> bestPlacePage = placeRepository.findBestPlaces(pageRequest);

        return new BestPlacePageResult(bestPlacePage.getContent(), bestPlacePage.getTotalElements(), bestPlacePage.getTotalPages(), bestPlacePage.getNumber(), bestPlacePage.getSize());
    }

    public LatestPlacePageResult findLatestPlaces(int page, int size, Long stationId, List<FoodType> foodTypes, List<Amenity> amenities) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<LatestPlaceItemDto> latestPlacePage = placeRepository.findLatestPlaces(pageRequest, stationId, foodTypes, amenities);

        return new LatestPlacePageResult(latestPlacePage.getContent(), latestPlacePage.getTotalElements(), latestPlacePage.getTotalPages(), latestPlacePage.getNumber(), latestPlacePage.getSize());
    }

    public List<EditorChoiceDto> findEditorChoices() {
        return placeChoiceRepository.findEditorChoice();
    }

    public EditorChoicePageResult findEditorChoices(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<EditorChoiceDto> editorChoicePage = placeChoiceRepository.findEditorChoice(pageRequest);

        return new EditorChoicePageResult(editorChoicePage.getContent(), editorChoicePage.getTotalElements(), editorChoicePage.getTotalPages(), editorChoicePage.getNumber(), editorChoicePage.getSize());
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

    public List<PlaceBannerImage> findPlaceBannerImages(Long placeId) {
        return placeBannerImageJpaRepository.findByPlaceIdOrderBySortAsc(placeId);
    }

    public PhotoCategoryImagePageResult findPlacePhotoCategoryImages(Long placePhotoCategoryId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sort").ascending());
        Page<PlacePhotoCategoryImage> photoPage;
        
        if (placePhotoCategoryId != null) {
            photoPage = placePhotoCategoryImageJpaRepository.findByPlacePhotoCategoryIdOrderBySortAsc(placePhotoCategoryId, pageRequest);
        } else {
            photoPage = placePhotoCategoryImageJpaRepository.findAll(pageRequest);
        }
        
        return new PhotoCategoryImagePageResult(
                photoPage.getContent(),
                photoPage.getTotalElements(),
                photoPage.getTotalPages(),
                photoPage.getNumber(),
                photoPage.getSize()
        );
    }


    public static class BestPlacePageResult {
        private final List<BestPlaceItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public BestPlacePageResult(List<BestPlaceItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<BestPlaceItemDto> getContent() {
            return content;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }
    }

    public static class EditorChoicePageResult {
        private final List<EditorChoiceDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public EditorChoicePageResult(List<EditorChoiceDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<EditorChoiceDto> getContent() {
            return content;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }
    }

    public static class LatestPlacePageResult {
        private final List<LatestPlaceItemDto> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public LatestPlacePageResult(List<LatestPlaceItemDto> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<LatestPlaceItemDto> getContent() {
            return content;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }
    }

    public static class PhotoCategoryImagePageResult {
        private final List<PlacePhotoCategoryImage> content;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        public PhotoCategoryImagePageResult(List<PlacePhotoCategoryImage> content, long totalElements, int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<PlacePhotoCategoryImage> getContent() {
            return content;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }
    }
}
