package com.tastyhouse.webapi.place;

import com.tastyhouse.core.entity.place.*;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import com.tastyhouse.core.entity.place.dto.EditorChoiceDto;
import com.tastyhouse.core.entity.place.dto.LatestPlaceItemDto;
import com.tastyhouse.core.entity.product.Product;
import com.tastyhouse.core.entity.product.dto.ProductSimpleDto;
import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.ReviewImage;
import com.tastyhouse.core.service.PlaceCoreService;
import com.tastyhouse.core.service.ProductCoreService;
import com.tastyhouse.core.service.ReviewCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.place.response.*;
import com.tastyhouse.webapi.place.request.LatestPlaceFilterRequest;
import com.tastyhouse.core.repository.place.PlaceBookmarkJpaRepository;
import com.tastyhouse.core.repository.place.PlaceOwnerMessageHistoryJpaRepository;
import com.tastyhouse.webapi.place.response.PlaceBookmarkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceCoreService placeCoreService;
    private final ProductCoreService productCoreService;
    private final ReviewCoreService reviewCoreService;
    private final PlaceBookmarkJpaRepository placeBookmarkJpaRepository;
    private final PlaceOwnerMessageHistoryJpaRepository placeOwnerMessageHistoryJpaRepository;

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

    public PlaceSummaryResponse getPlaceSummary(Long placeId) {
        Place place = placeCoreService.findPlaceById(placeId);

        return PlaceSummaryResponse.builder()
                .id(place.getId())
                .name(place.getPlaceName())
                .roadAddress(place.getRoadAddress())
                .lotAddress(place.getLotAddress())
                .rating(place.getRating())
                .build();
    }

    public PlaceInfoResponse getPlaceInfo(Long placeId) {
        Place place = placeCoreService.findPlaceById(placeId);
        PlaceStation station = placeCoreService.findStationById(place.getStationId());
        List<PlaceBusinessHour> businessHours = placeCoreService.findPlaceBusinessHours(placeId);
        List<PlaceBreakTime> breakTimes = placeCoreService.findPlaceBreakTimes(placeId);
        List<PlaceClosedDay> closedDays = placeCoreService.findPlaceClosedDays(placeId);

        List<PlaceInfoResponse.BusinessHourItem> businessHourItems = businessHours.stream()
                .map(this::convertToBusinessHourItem)
                .toList();

        List<PlaceInfoResponse.BreakTimeItem> breakTimeItems = breakTimes.stream()
                .map(this::convertToBreakTimeItem)
                .toList();

        List<PlaceInfoResponse.ClosedDayItem> closedDayItems = closedDays.stream()
                .map(this::convertToClosedDayItem)
                .toList();

        return PlaceInfoResponse.builder()
                .id(place.getId())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .stationName(station.getStationName())
                .phoneNumber(place.getPhoneNumber())
                .closedDays(closedDayItems)
                .businessHours(businessHourItems)
                .breakTimes(breakTimeItems)
                .build();
    }

    public List<PlaceThumbnailResponse> getPlaceThumbnails(Long placeId) {
        List<PlaceImage> thumbnails = placeCoreService.findPlaceThumbnails(placeId);
        return thumbnails.stream()
                .map(this::convertToPlaceThumbnailResponse)
                .toList();
    }

    public List<PlaceMenuResponse> getPlaceMenus(Long placeId) {
        List<Product> products = productCoreService.findProductsByPlaceId(placeId);
        return products.stream()
                .map(this::convertToPlaceMenuResponse)
                .toList();
    }

    public PageResult<PlacePhotoResponse> getPlacePhotos(Long placeId, PlaceImageCategory category, PageRequest pageRequest) {
        Page<PlaceImage> photoPage = placeCoreService.findPlacePhotos(placeId, category, pageRequest.getPage(), pageRequest.getSize());

        List<PlacePhotoResponse> photos = photoPage.getContent().stream()
                .map(this::convertToPlacePhotoResponse)
                .toList();

        return new PageResult<>(photos, photoPage.getTotalElements(), photoPage.getTotalPages(), photoPage.getNumber(), photoPage.getSize());
    }

    public PageResult<PlaceReviewResponse> getPlaceReviews(Long placeId, Integer rating, PageRequest pageRequest) {
        ReviewCoreService.PlaceReviewPageResult reviewPageResult = reviewCoreService.findPlaceReviews(placeId, rating, pageRequest.getPage(), pageRequest.getSize());

        List<Long> reviewIds = reviewPageResult.getContent().stream()
                .map(Review::getId)
                .toList();

        Map<Long, List<ReviewImage>> reviewImagesMap = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            List<ReviewImage> allImages = reviewCoreService.findReviewImages(reviewIds);
            reviewImagesMap = allImages.stream()
                    .collect(Collectors.groupingBy(ReviewImage::getReviewId));
        }

        List<PlaceReviewResponse> reviews = new ArrayList<>();
        for (Review review : reviewPageResult.getContent()) {
            List<ReviewImage> images = reviewImagesMap.getOrDefault(review.getId(), new ArrayList<>());
            reviews.add(convertToPlaceReviewResponse(review, images));
        }

        return new PageResult<>(reviews, reviewPageResult.getTotalElements(), reviewPageResult.getTotalPages(), reviewPageResult.getCurrentPage(), reviewPageResult.getPageSize());
    }

    public PlaceReviewStatisticsResponse getPlaceReviewStatistics(Long placeId) {
        Map<String, Object> statistics = reviewCoreService.getPlaceReviewStatistics(placeId);

        Place place = placeCoreService.findPlaceById(placeId);

        return PlaceReviewStatisticsResponse.builder()
                .totalRating(place.getRating())
                .totalReviewCount((Long) statistics.get("totalReviewCount"))
                .averageTasteRating((Double) statistics.get("averageTasteRating"))
                .averageAmountRating((Double) statistics.get("averageAmountRating"))
                .averagePriceRating((Double) statistics.get("averagePriceRating"))
                .averageAtmosphereRating((Double) statistics.get("averageAtmosphereRating"))
                .averageKindnessRating((Double) statistics.get("averageKindnessRating"))
                .averageHygieneRating((Double) statistics.get("averageHygieneRating"))
                .willRevisitPercentage((Double) statistics.get("willRevisitPercentage"))
                .monthlyReviewCounts((Map<Integer, Long>) statistics.get("monthlyReviewCounts"))
                .ratingCounts((Map<Integer, Long>) statistics.get("ratingCounts"))
                .build();
    }

    private PlaceInfoResponse.BusinessHourItem convertToBusinessHourItem(PlaceBusinessHour businessHour) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return PlaceInfoResponse.BusinessHourItem.builder()
                .dayType(businessHour.getDayType().name())
                .dayTypeDescription(businessHour.getDayType().getDescription())
                .openTime(businessHour.getOpenTime() != null ? businessHour.getOpenTime().format(formatter) : null)
                .closeTime(businessHour.getCloseTime() != null ? businessHour.getCloseTime().format(formatter) : null)
                .isClosed(businessHour.getIsClosed())
                .build();
    }

    private PlaceInfoResponse.BreakTimeItem convertToBreakTimeItem(PlaceBreakTime breakTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return PlaceInfoResponse.BreakTimeItem.builder()
                .dayType(breakTime.getDayType().name())
                .dayTypeDescription(breakTime.getDayType().getDescription())
                .startTime(breakTime.getStartTime() != null ? breakTime.getStartTime().format(formatter) : null)
                .endTime(breakTime.getEndTime() != null ? breakTime.getEndTime().format(formatter) : null)
                .build();
    }

    private PlaceInfoResponse.ClosedDayItem convertToClosedDayItem(PlaceClosedDay closedDay) {
        return PlaceInfoResponse.ClosedDayItem.builder()
                .closedDayType(closedDay.getClosedDayType().name())
                .description(closedDay.getClosedDayType().getDescription())
                .build();
    }

    private PlaceThumbnailResponse convertToPlaceThumbnailResponse(PlaceImage image) {
        return PlaceThumbnailResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .sort(image.getSort())
                .build();
    }

    private PlaceMenuResponse convertToPlaceMenuResponse(Product product) {
        return PlaceMenuResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .originalPrice(product.getOriginalPrice())
                .discountPrice(product.getDiscountPrice())
                .discountRate(product.getDiscountRate())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .isRepresentative(product.getIsRepresentative())
                .build();
    }

    private PlacePhotoResponse convertToPlacePhotoResponse(PlaceImage image) {
        return PlacePhotoResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .categoryCode(image.getImageCategory() != null ? image.getImageCategory().name() : null)
                .categoryName(image.getImageCategory() != null ? image.getImageCategory().getDescription() : null)
                .sort(image.getSort())
                .build();
    }

    private PlaceReviewResponse convertToPlaceReviewResponse(Review review, List<ReviewImage> images) {
        List<PlaceReviewResponse.ReviewImageItem> imageItems = images.stream()
                .map(img -> PlaceReviewResponse.ReviewImageItem.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .sort(img.getSort())
                        .build())
                .toList();

        return PlaceReviewResponse.builder()
                .id(review.getId())
                .memberId(review.getMemberId())
                .memberNickname(null)
                .content(review.getContent())
                .totalRating(review.getTotalRating())
                .tasteRating(review.getTasteRating())
                .amountRating(review.getAmountRating())
                .priceRating(review.getPriceRating())
                .atmosphereRating(review.getAtmosphereRating())
                .kindnessRating(review.getKindnessRating())
                .hygieneRating(review.getHygieneRating())
                .willRevisit(review.getWillRevisit())
                .images(imageItems)
                .createdAt(review.getCreatedAt())
                .build();
    }

    public PlaceBookmarkResponse isBookmarked(Long placeId, Long memberId) {
        boolean isBookmarked = placeBookmarkJpaRepository.existsByPlaceIdAndMemberId(placeId, memberId);
        return new PlaceBookmarkResponse(isBookmarked);
    }

    @Transactional
    public boolean toggleBookmark(Long placeId, Long memberId) {
        if (placeBookmarkJpaRepository.existsByPlaceIdAndMemberId(placeId, memberId)) {
            placeBookmarkJpaRepository.deleteByPlaceIdAndMemberId(placeId, memberId);
            return false;
        } else {
            placeCoreService.findPlaceById(placeId); // Ensure place exists
            PlaceBookmark bookmark = new PlaceBookmark(placeId, memberId);
            placeBookmarkJpaRepository.save(bookmark);
            return true;
        }
    }

    public PlaceOwnerMessageHistoryResponse getPlaceOwnerMessageHistory(Long placeId) {
        placeCoreService.findPlaceById(placeId); // Ensure place exists

        return placeOwnerMessageHistoryJpaRepository.findFirstByPlaceIdOrderByCreatedAtDesc(placeId)
                .map(history -> PlaceOwnerMessageHistoryResponse.builder()
                        .message(history.getMessage())
                        .createdAt(history.getCreatedAt())
                        .build())
                .orElse(PlaceOwnerMessageHistoryResponse.builder()
                        .message(null)
                        .createdAt(null)
                        .build());
    }
}
