package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import com.tastyhouse.core.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceCoreService {

    private final PlaceRepository placeRepository;

    public List<Place> findNearbyPlaces(Double latitude, Double longitude) {
        BigDecimal lat = BigDecimal.valueOf(latitude);
        BigDecimal lon = BigDecimal.valueOf(longitude);
        return placeRepository.findNearbyPlaces(lat, lon);
    }

    public BestPlacePageResult findBestPlaces(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BestPlaceItemDto> bestPlacePage = placeRepository.findBestPlaces(pageRequest);

        return new BestPlacePageResult(
            bestPlacePage.getContent(),
            bestPlacePage.getTotalElements(),
            bestPlacePage.getTotalPages(),
            bestPlacePage.getNumber(),
            bestPlacePage.getSize()
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

        public List<BestPlaceItemDto> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
}
