package com.tastyhouse.webapi.product;

import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.product.*;
import com.tastyhouse.core.entity.product.dto.TodayDiscountProductDto;
import com.tastyhouse.core.service.PlaceCoreService;
import com.tastyhouse.core.service.ProductCoreService;
import com.tastyhouse.webapi.common.PageRequest;
import com.tastyhouse.webapi.common.PageResult;
import com.tastyhouse.webapi.product.response.ProductCategoryListItem;
import com.tastyhouse.webapi.product.response.ProductDetailResponse;
import com.tastyhouse.webapi.product.response.ProductListItem;
import com.tastyhouse.webapi.product.response.TodayDiscountProductItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCoreService productCoreService;
    private final PlaceCoreService placeCoreService;

    public PageResult<TodayDiscountProductItem> findTodayDiscountProducts(PageRequest pageRequest) {
        ProductCoreService.TodayDiscountProductPageResult coreResult = productCoreService.findTodayDiscountProducts(
            pageRequest.getPage(), pageRequest.getSize()
        );

        List<TodayDiscountProductItem> todayDiscountProductItems = coreResult.getContent().stream()
            .map(this::convertToTodayDiscountProductItem)
            .toList();

        return new PageResult<>(
            todayDiscountProductItems,
            coreResult.getTotalElements(),
            coreResult.getTotalPages(),
            coreResult.getCurrentPage(),
            coreResult.getPageSize()
        );
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
            .categoryType(category.getCategoryType().name())
            .displayName(category.getDisplayName())
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
            .collect(Collectors.toMap(ProductCategory::getId, ProductCategory::getDisplayName));
    }

    private ProductListItem convertToProductListItem(Product product, Map<Long, String> categoryNameMap) {
        String categoryName = product.getProductCategoryId() != null
            ? categoryNameMap.get(product.getProductCategoryId())
            : null;

        return ProductListItem.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .imageUrl(product.getImageUrl())
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
                .map(ProductCategory::getDisplayName)
                .orElse(null);
        }

        List<ProductDetailResponse.OptionGroupResponse> optionGroups = buildOptionGroups(product);

        return ProductDetailResponse.builder()
            .id(product.getId())
            .placeId(product.getPlaceId())
            .placeName(placeName)
            .name(product.getName())
            .description(product.getDescription())
            .imageUrl(product.getImageUrl())
            .originalPrice(product.getOriginalPrice())
            .discountPrice(product.getDiscountPrice())
            .discountRate(product.getDiscountRate())
            .rating(product.getRating())
            .reviewCount(product.getReviewCount())
            .isRepresentative(product.getIsRepresentative())
            .isSoldOut(product.getIsSoldOut())
            .categoryName(categoryName)
            .optionGroups(optionGroups)
            .build();
    }

    private List<ProductDetailResponse.OptionGroupResponse> buildOptionGroups(Product product) {
        List<ProductDetailResponse.OptionGroupResponse> result = new ArrayList<>();

        // 상품별 옵션 그룹
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

        // 공통 옵션 그룹
        List<ProductCommonOptionGroup> productCommonOptionGroups = productCoreService.findProductCommonOptionGroupsByProductId(product.getId());
        if (!productCommonOptionGroups.isEmpty()) {
            List<Long> commonOptionGroupIds = productCommonOptionGroups.stream()
                .map(ProductCommonOptionGroup::getCommonOptionGroupId)
                .toList();
            List<CommonOptionGroup> commonOptionGroups = productCoreService.findCommonOptionGroupsByIds(commonOptionGroupIds);
            List<CommonOption> commonOptions = productCoreService.findCommonOptionsByOptionGroupIds(commonOptionGroupIds);
            Map<Long, List<CommonOption>> commonOptionsByGroupId = commonOptions.stream()
                .collect(Collectors.groupingBy(CommonOption::getOptionGroupId));

            for (CommonOptionGroup group : commonOptionGroups) {
                List<ProductDetailResponse.OptionResponse> options = commonOptionsByGroupId
                    .getOrDefault(group.getId(), Collections.emptyList())
                    .stream()
                    .map(this::convertCommonOptionToOptionResponse)
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

    private ProductDetailResponse.OptionResponse convertCommonOptionToOptionResponse(CommonOption option) {
        return ProductDetailResponse.OptionResponse.builder()
            .id(option.getId())
            .name(option.getName())
            .additionalPrice(option.getAdditionalPrice())
            .isSoldOut(option.getIsSoldOut())
            .build();
    }
}
