package com.tastyhouse.webapi.place;

import com.tastyhouse.core.entity.place.*;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import com.tastyhouse.core.entity.place.dto.EditorChoiceDto;
import com.tastyhouse.core.entity.place.dto.LatestPlaceItemDto;
import com.tastyhouse.core.entity.product.Product;
import com.tastyhouse.core.entity.product.ProductCategory;
import com.tastyhouse.core.entity.product.dto.ProductSimpleDto;
import com.tastyhouse.core.entity.review.Review;
import com.tastyhouse.core.entity.review.ReviewImage;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.repository.place.PlaceBookmarkJpaRepository;
import com.tastyhouse.core.repository.place.PlaceOwnerMessageHistoryJpaRepository;
import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.common.ReviewsByRatingResult;
import com.tastyhouse.core.service.PlaceCoreService;
import com.tastyhouse.core.service.ProductCoreService;
import com.tastyhouse.core.service.ReviewCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.place.request.LatestPlaceFilterRequest;
import com.tastyhouse.webapi.place.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        org.springframework.data.domain.Page<BestPlaceItemDto> page =
            placeCoreService.findBestPlaces(pageRequest.getPage(), pageRequest.getSize());

        List<BestPlaceListItem> bestPlaceListItems = page.getContent().stream().map(this::convertToBestPlaceListItem).toList();

        return new PageResult<>(bestPlaceListItems, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }

    public PageResult<LatestPlaceListItem> findLatestPlaces(PageRequest pageRequest, LatestPlaceFilterRequest filterRequest) {
        org.springframework.data.domain.Page<LatestPlaceItemDto> page = placeCoreService.findLatestPlaces(
                pageRequest.getPage(),
                pageRequest.getSize(),
                filterRequest.stationId(),
                filterRequest.foodTypes(),
                filterRequest.amenities()
        );

        List<LatestPlaceListItem> latestPlaceListItems = page.getContent().stream().map(this::convertToLatestPlaceListItem).toList();

        return new PageResult<>(latestPlaceListItems, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }

    public List<EditorChoiceResponse> findEditorChoices() {
        return placeCoreService.findEditorChoices().stream().map(this::convertToEditorChoiceResponse).toList();
    }

    public List<EditorChoiceResponse> findEditorChoices(PageRequest pageRequest) {
        return placeCoreService.findEditorChoices(pageRequest.getPage(), pageRequest.getSize())
            .getContent().stream().map(this::convertToEditorChoiceResponse).toList();
    }

    private EditorChoiceResponse convertToEditorChoiceResponse(EditorChoiceDto dto) {
        List<EditorChoiceProductItem> productItems = dto.getProducts() != null ? dto.getProducts().stream().map(this::convertToEditorChoiceProductItem).toList() : new ArrayList<EditorChoiceProductItem>();

        return EditorChoiceResponse.builder().id(dto.getId()).name(dto.getName()).imageUrl(dto.getPlaceImageUrl()).title(dto.getTitle()).content(dto.getContent()).products(productItems).build();
    }

    private BestPlaceListItem convertToBestPlaceListItem(BestPlaceItemDto dto) {
        return BestPlaceListItem.builder().id(dto.getId()).name(dto.getName()).stationName(dto.getStationName()).rating(dto.getRating()).imageUrl(dto.getImageUrl()).foodTypes(dto.getFoodTypes()).build();
    }

    private LatestPlaceListItem convertToLatestPlaceListItem(LatestPlaceItemDto dto) {
        return LatestPlaceListItem.builder().id(dto.getId()).name(dto.getName()).stationName(dto.getStationName()).rating(dto.getRating()).imageUrl(dto.getImageUrl()).createdAt(dto.getCreatedAt()).reviewCount(dto.getReviewCount()).bookmarkCount(dto.getBookmarkCount()).foodTypes(dto.getFoodTypes()).build();
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
                .name(place.getName())
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
        List<PlaceAmenity> placeAmenities = placeCoreService.findPlaceAmenities(placeId);

        List<PlaceInfoResponse.BusinessHourItem> businessHourItems = businessHours.stream()
                .map(this::convertToBusinessHourItem)
                .toList();

        List<PlaceInfoResponse.BreakTimeItem> breakTimeItems = breakTimes.stream()
                .map(this::convertToBreakTimeItem)
                .toList();

        List<PlaceInfoResponse.ClosedDayItem> closedDayItems = closedDays.stream()
                .map(this::convertToClosedDayItem)
                .toList();

        List<PlaceInfoResponse.AmenityItem> amenityItems = placeAmenities.stream()
                .map(this::convertToAmenityItem)
                .toList();

        // 사장님 한마디 히스토리 조회
        String ownerMessage = null;
        java.time.LocalDateTime ownerMessageCreatedAt = null;
        var ownerMessageHistory = placeOwnerMessageHistoryJpaRepository.findFirstByPlaceIdOrderByCreatedAtDesc(placeId);
        if (ownerMessageHistory.isPresent()) {
            ownerMessage = ownerMessageHistory.get().getMessage();
            ownerMessageCreatedAt = ownerMessageHistory.get().getCreatedAt();
        }

        return PlaceInfoResponse.builder()
                .id(place.getId())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .stationName(station.getStationName())
                .phoneNumber(place.getPhoneNumber())
                .closedDays(closedDayItems)
                .businessHours(businessHourItems)
                .breakTimes(breakTimeItems)
                .amenities(amenityItems)
                .ownerMessage(ownerMessage)
                .ownerMessageCreatedAt(ownerMessageCreatedAt)
                .build();
    }

    public List<PlaceBannerResponse> getPlaceBanners(Long placeId) {
        List<PlaceBannerImage> banners = placeCoreService.findPlaceBannerImages(placeId);
        return banners.stream()
                .map(this::convertToPlaceBannerResponse)
                .toList();
    }

    public List<PlaceMenuCategoryResponse> getPlaceMenus(Long placeId) {
        List<ProductCategory> categories = productCoreService.findProductCategoriesByPlaceId(placeId);
        List<Product> products = productCoreService.findProductsByPlaceId(placeId);

        // 카테고리 ID별로 Product 그룹화
        Map<Long, List<Product>> productsByCategory = products.stream()
                .filter(product -> product.getProductCategoryId() != null)
                .collect(Collectors.groupingBy(Product::getProductCategoryId));

        // 카테고리 순서대로 응답 생성
        return categories.stream()
                .map(category -> {
                    List<Product> categoryProducts = productsByCategory.getOrDefault(category.getId(), new ArrayList<Product>());
                    List<PlaceMenuResponse> menuResponses = categoryProducts.stream()
                            .map(this::convertToPlaceMenuResponse)
                            .toList();
                    return PlaceMenuCategoryResponse.builder()
                            .categoryName(category.getName())
                            .menus(menuResponses)
                            .build();
                })
                .toList();
    }

    public List<PlacePhotoCategoryResponse> getPlacePhotos(Long placeId) {
        List<PlacePhotoCategory> categories = placeCoreService.findAllPlacePhotoCategories();
        List<PlacePhotoCategoryImage> images = placeCoreService.findAllPlacePhotoCategoryImages();

        // 카테고리 ID별로 이미지 그룹화
        Map<Long, List<PlacePhotoCategoryImage>> imagesByCategory = images.stream()
                .filter(image -> image.getPlacePhotoCategoryId() != null)
                .collect(Collectors.groupingBy(PlacePhotoCategoryImage::getPlacePhotoCategoryId));

        // 카테고리 순서대로 응답 생성
        return categories.stream()
                .map(category -> {
                    List<PlacePhotoCategoryImage> categoryImages = imagesByCategory.getOrDefault(category.getId(), new ArrayList<PlacePhotoCategoryImage>());
                    List<String> imageUrls = categoryImages.stream()
                            .map(PlacePhotoCategoryImage::getImageUrl)
                            .toList();
                    return PlacePhotoCategoryResponse.builder()
                            .name(category.getName())
                            .imageUrls(imageUrls)
                            .build();
                })
                .toList();
    }

    public static class PlaceReviewsByRatingWithPagination {
        private final PlaceReviewsByRatingResponse response;
        private final long totalElements;

        public PlaceReviewsByRatingWithPagination(PlaceReviewsByRatingResponse response, long totalElements) {
            this.response = response;
            this.totalElements = totalElements;
        }

        public PlaceReviewsByRatingResponse getResponse() {
            return response;
        }

        public long getTotalElements() {
            return totalElements;
        }
    }

    public PlaceReviewsByRatingWithPagination getPlaceReviewsByRatingWithPagination(Long placeId, int page, int size) {
        ReviewsByRatingResult result = reviewCoreService.getPlaceReviewsByRating(placeId, page, size);

        Map<Integer, List<PlaceReviewListItem>> reviewsByRating = result.getReviewsByRating().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry<Integer, List<LatestReviewListItemDto>>::getKey,
                        entry -> entry.getValue().stream()
                                .map(this::convertToPlaceReviewListItem)
                                .toList()
                ));

        List<PlaceReviewListItem> allReviews = result.getAllReviews().stream()
                .map(this::convertToPlaceReviewListItem)
                .toList();

        PlaceReviewsByRatingResponse response = PlaceReviewsByRatingResponse.builder()
                .reviewsByRating(reviewsByRating)
                .allReviews(allReviews)
                .totalReviewCount(result.getTotalReviewCount())
                .build();

        return new PlaceReviewsByRatingWithPagination(response, result.getTotalElements());
    }

    private PlaceReviewListItem convertToPlaceReviewListItem(LatestReviewListItemDto dto) {
        return PlaceReviewListItem.builder()
                .id(dto.getId())
                .imageUrls(dto.getImageUrls())
                .totalRating(dto.getTotalRating())
                .content(dto.getContent())
                .memberNickname(dto.getMemberNickname())
                .memberProfileImageUrl(dto.getMemberProfileImageUrl())
                .createdAt(dto.getCreatedAt())
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .build();
    }

    @SuppressWarnings("unchecked")
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

    private PlaceInfoResponse.AmenityItem convertToAmenityItem(PlaceAmenity placeAmenity) {
        PlaceAmenityCategory category = placeCoreService.findPlaceAmenityCategoryById(placeAmenity.getPlaceAmenityCategoryId());
        return PlaceInfoResponse.AmenityItem.builder()
                .code(category.getAmenity().name())
                .name(category.getDisplayName())
                .imageUrlOn(category.getImageUrlOn())
                .build();
    }

    private PlaceBannerResponse convertToPlaceBannerResponse(PlaceBannerImage image) {
        return PlaceBannerResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .sort(image.getSort())
                .build();
    }

    private PlaceMenuResponse convertToPlaceMenuResponse(Product product) {
        String imageUrl = getFirstImageUrl(product.getId());

        return PlaceMenuResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(imageUrl)
                .originalPrice(product.getOriginalPrice())
                .discountPrice(product.getDiscountPrice())
                .discountRate(product.getDiscountRate())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .isRepresentative(product.getIsRepresentative())
                .spiciness(product.getSpiciness())
                .build();
    }

    private String getFirstImageUrl(Long productId) {
        return productCoreService.getFirstImageUrl(productId);
    }

    private PlacePhotoResponse convertToPlacePhotoResponse(PlacePhotoCategoryImage image) {
        String categoryName = null;
        if (image.getPlacePhotoCategoryId() != null) {
            PlacePhotoCategory category = placeCoreService.findPlaceImageCategoryById(image.getPlacePhotoCategoryId());
            categoryName = category.getName();
        }
        return PlacePhotoResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .categoryCode(null) // Entity 기반으로 변경되어 code는 사용하지 않음
                .categoryName(categoryName)
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

    public PlaceNameResponse getPlaceName(Long placeId) {
        Place place = placeCoreService.findPlaceById(placeId);
        return PlaceNameResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .build();
    }

    public PlaceOrderMethodResponse getPlaceOrderMethods(Long placeId) {
        placeCoreService.findPlaceById(placeId); // Ensure place exists
        List<PlaceOrderMethod> placeOrderMethods = placeCoreService.findPlaceOrderMethods(placeId);

        List<PlaceOrderMethodResponse.OrderMethodItem> orderMethodItems = placeOrderMethods.stream()
                .map(pom -> PlaceOrderMethodResponse.OrderMethodItem.builder()
                        .code(pom.getOrderMethod().name())
                        .name(pom.getOrderMethod().getDisplayName())
                        .build())
                .toList();

        return PlaceOrderMethodResponse.builder()
                .placeId(placeId)
                .orderMethods(orderMethodItems)
                .build();
    }
}
