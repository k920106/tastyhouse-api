package com.tastyhouse.webapi.product;

import com.tastyhouse.core.common.PageResult;
import com.tastyhouse.core.common.ReviewsByRatingResult;
import com.tastyhouse.core.entity.product.*;
import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import com.tastyhouse.core.entity.review.dto.LatestReviewListItemDto;
import com.tastyhouse.core.repository.product.ProductImageJpaRepository;
import com.tastyhouse.core.service.PlaceCoreService;
import com.tastyhouse.core.service.ProductCoreService;
import com.tastyhouse.core.service.ReviewCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.product.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCoreService productCoreService;
    private final PlaceCoreService placeCoreService;
    private final ProductImageJpaRepository productImageJpaRepository;
    private final ReviewCoreService reviewCoreService;

    public PageResult<TodayDiscountProductItem> findTodayDiscountProducts(PageRequest pageRequest) {
        return productCoreService.findTodayDiscountProducts(
            pageRequest.getPage(), pageRequest.getSize()
        ).map(this::convertToTodayDiscountProductItem);
    }

    private TodayDiscountProductItem convertToTodayDiscountProductItem(TodayDiscountProductDto dto) {
        return TodayDiscountProductItem.builder()
            .id(dto.getId())
            .placeName(dto.getPlaceName())
            .name(dto.getName())
            .imageUrl(dto.getImageUrl())
            .originalPrice(dto.getOriginalPrice())
            .discountPrice(dto.getDiscountPrice())
            .discountRate(dto.getDiscountRate())
            .build();
    }

    public List<ProductListItem> findProductsByPlaceId(Long placeId) {
        List<Product> products = productCoreService.findActiveProductsByPlaceId(placeId);
        Map<Long, String> categoryNameMap = buildCategoryNameMap(placeId);

        return products.stream()
            .map(product -> convertToProductListItem(product, categoryNameMap))
            .toList();
    }

    public List<ProductCategoryListItem> findProductCategoriesByPlaceId(Long placeId) {
        List<ProductCategory> categories = productCoreService.findProductCategoriesByPlaceId(placeId);
        return categories.stream()
            .map(this::convertToProductCategoryListItem)
            .toList();
    }

    private ProductCategoryListItem convertToProductCategoryListItem(ProductCategory category) {
        return ProductCategoryListItem.builder()
            .id(category.getId())
            .displayName(category.getName())
            .sort(category.getSort())
            .build();
    }

    public Optional<ProductDetailResponse> findProductById(Long productId) {
        return productCoreService.findProductById(productId)
            .map(this::buildProductDetailResponse);
    }

    private Map<Long, String> buildCategoryNameMap(Long placeId) {
        List<ProductCategory> categories = productCoreService.findProductCategoriesByPlaceId(placeId);
        return categories.stream()
            .collect(Collectors.toMap(ProductCategory::getId, ProductCategory::getName));
    }

    private ProductListItem convertToProductListItem(Product product, Map<Long, String> categoryNameMap) {
        String categoryName = product.getProductCategoryId() != null
            ? categoryNameMap.get(product.getProductCategoryId())
            : null;

        String imageUrl = getFirstImageUrl(product.getId());

        return ProductListItem.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .imageUrl(imageUrl)
            .originalPrice(product.getOriginalPrice())
            .discountPrice(product.getDiscountPrice())
            .discountRate(product.getDiscountRate())
            .rating(product.getRating())
            .reviewCount(product.getReviewCount())
            .isRepresentative(product.getIsRepresentative())
            .isSoldOut(product.getIsSoldOut())
            .categoryName(categoryName)
            .build();
    }

    private ProductDetailResponse buildProductDetailResponse(Product product) {
        String placeName = placeCoreService.findPlaceById(product.getPlaceId()).getName();

        String categoryName = null;
        if (product.getProductCategoryId() != null) {
            categoryName = productCoreService.findProductCategoryById(product.getProductCategoryId())
                .map(ProductCategory::getName)
                .orElse(null);
        }

        List<ProductDetailResponse.OptionGroupResponse> optionGroups = buildOptionGroups(product);

        List<String> imageUrls = getAllImageUrls(product.getId());

        Map<String, Object> reviewStatistics = reviewCoreService.getProductReviewStatistics(product.getId());
        Long totalReviewCount = (Long) reviewStatistics.get("totalReviewCount");
        Integer reviewCount = totalReviewCount != null ? totalReviewCount.intValue() : 0;

        return ProductDetailResponse.builder()
            .id(product.getId())
            .placeId(product.getPlaceId())
            .placeName(placeName)
            .name(product.getName())
            .description(product.getDescription())
            .imageUrls(imageUrls)
            .originalPrice(product.getOriginalPrice())
            .discountPrice(product.getDiscountPrice())
            .discountRate(product.getDiscountRate())
            .rating(product.getRating())
            .reviewCount(reviewCount)
            .isRepresentative(product.getIsRepresentative())
            .isSoldOut(product.getIsSoldOut())
            .categoryName(categoryName)
            .optionGroups(optionGroups)
            .build();
    }

    private List<ProductDetailResponse.OptionGroupResponse> buildOptionGroups(Product product) {
        List<ProductDetailResponse.OptionGroupResponse> result = new ArrayList<>();

        List<ProductOptionGroup> productOptionGroups = productCoreService.findProductOptionGroupsByProductId(product.getId());
        if (!productOptionGroups.isEmpty()) {
            List<Long> optionGroupIds = productOptionGroups.stream()
                .map(ProductOptionGroup::getId)
                .toList();
            List<ProductOption> productOptions = productCoreService.findProductOptionsByOptionGroupIds(optionGroupIds);
            Map<Long, List<ProductOption>> optionsByGroupId = productOptions.stream()
                .collect(Collectors.groupingBy(ProductOption::getOptionGroupId));

            for (ProductOptionGroup group : productOptionGroups) {
                List<ProductDetailResponse.OptionResponse> options = optionsByGroupId
                    .getOrDefault(group.getId(), Collections.emptyList())
                    .stream()
                    .map(this::convertToOptionResponse)
                    .toList();

                result.add(ProductDetailResponse.OptionGroupResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .description(group.getDescription())
                    .isRequired(group.getIsRequired())
                    .isMultipleSelect(group.getIsMultipleSelect())
                    .minSelect(group.getMinSelect())
                    .maxSelect(group.getMaxSelect())
                    .isCommon(false)
                    .options(options)
                    .build());
            }
        }

        List<ProductCommonOptionGroup> productCommonOptionGroups = productCoreService.findProductCommonOptionGroupsByProductId(product.getId());
        if (!productCommonOptionGroups.isEmpty()) {
            List<Long> commonOptionGroupIds = productCommonOptionGroups.stream()
                .map(ProductCommonOptionGroup::getId)
                .toList();
            List<ProductCommonOption> commonOptions = productCoreService.findProductCommonOptionsByOptionGroupIds(commonOptionGroupIds);
            Map<Long, List<ProductCommonOption>> commonOptionsByGroupId = commonOptions.stream()
                .collect(Collectors.groupingBy(ProductCommonOption::getOptionGroupId));

            for (ProductCommonOptionGroup group : productCommonOptionGroups) {
                List<ProductDetailResponse.OptionResponse> options = commonOptionsByGroupId
                    .getOrDefault(group.getId(), Collections.emptyList())
                    .stream()
                    .map(this::convertProductCommonOptionToOptionResponse)
                    .toList();

                result.add(ProductDetailResponse.OptionGroupResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .description(group.getDescription())
                    .isRequired(group.getIsRequired())
                    .isMultipleSelect(group.getIsMultipleSelect())
                    .minSelect(group.getMinSelect())
                    .maxSelect(group.getMaxSelect())
                    .isCommon(true)
                    .options(options)
                    .build());
            }
        }

        return result;
    }

    private ProductDetailResponse.OptionResponse convertToOptionResponse(ProductOption option) {
        return ProductDetailResponse.OptionResponse.builder()
            .id(option.getId())
            .name(option.getName())
            .additionalPrice(option.getAdditionalPrice())
            .isSoldOut(option.getIsSoldOut())
            .build();
    }

    private ProductDetailResponse.OptionResponse convertProductCommonOptionToOptionResponse(ProductCommonOption option) {
        return ProductDetailResponse.OptionResponse.builder()
            .id(option.getId())
            .name(option.getName())
            .additionalPrice(option.getAdditionalPrice())
            .isSoldOut(option.getIsSoldOut())
            .build();
    }

    private String getFirstImageUrl(Long productId) {
        return productImageJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(productId)
            .stream()
            .findFirst()
            .map(ProductImage::getImageUrl)
            .orElse(null);
    }

    private List<String> getAllImageUrls(Long productId) {
        return productImageJpaRepository.findByProductIdAndIsActiveTrueOrderBySortAsc(productId)
            .stream()
            .map(ProductImage::getImageUrl)
            .toList();
    }

    public ProductReviewsByRatingWithPagination getProductReviewsByRatingWithPagination(Long productId, int page, int size) {
        ReviewsByRatingResult result = reviewCoreService.getProductReviewsByRating(productId, page, size);

        Map<Integer, List<ProductReviewListItem>> reviewsByRating = result.getReviewsByRating().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry<Integer, List<LatestReviewListItemDto>>::getKey,
                        entry -> entry.getValue().stream()
                                .map(this::convertToProductReviewListItem)
                                .toList()
                ));

        List<ProductReviewListItem> allReviews = result.getAllReviews().stream()
                .map(this::convertToProductReviewListItem)
                .toList();

        ProductReviewsByRatingResponse response = ProductReviewsByRatingResponse.builder()
                .reviewsByRating(reviewsByRating)
                .allReviews(allReviews)
                .totalReviewCount(result.getTotalReviewCount())
                .build();

        return new ProductReviewsByRatingWithPagination(response, result.getTotalElements());
    }

    private ProductReviewListItem convertToProductReviewListItem(LatestReviewListItemDto dto) {
        return ProductReviewListItem.builder()
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

    public ProductReviewStatisticsResponse getProductReviewStatistics(Long productId) {
        Map<String, Object> statistics = reviewCoreService.getProductReviewStatistics(productId);

        Product product = productCoreService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        return ProductReviewStatisticsResponse.builder()
                .totalRating(product.getRating())
                .totalReviewCount((Long) statistics.get("totalReviewCount"))
                .averageTasteRating((Double) statistics.get("averageTasteRating"))
                .averageAmountRating((Double) statistics.get("averageAmountRating"))
                .averagePriceRating((Double) statistics.get("averagePriceRating"))
                .build();
    }

    public static class ProductReviewsByRatingWithPagination {
        private final ProductReviewsByRatingResponse response;
        private final Long totalElements;

        public ProductReviewsByRatingWithPagination(ProductReviewsByRatingResponse response, Long totalElements) {
            this.response = response;
            this.totalElements = totalElements;
        }

        public ProductReviewsByRatingResponse getResponse() {
            return response;
        }

        public Long getTotalElements() {
            return totalElements;
        }
    }
}
