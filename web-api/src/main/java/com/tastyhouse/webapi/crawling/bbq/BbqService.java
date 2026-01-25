package com.tastyhouse.webapi.crawling.bbq;

import com.tastyhouse.core.entity.product.Product;
import com.tastyhouse.core.entity.product.ProductCategory;
import com.tastyhouse.core.entity.product.ProductOption;
import com.tastyhouse.core.entity.product.ProductOptionGroup;
import com.tastyhouse.core.repository.product.ProductCategoryJpaRepository;
import com.tastyhouse.core.repository.product.ProductJpaRepository;
import com.tastyhouse.core.repository.product.ProductOptionGroupJpaRepository;
import com.tastyhouse.core.repository.product.ProductOptionJpaRepository;
import com.tastyhouse.external.bbq.BbqApiClient;
import com.tastyhouse.external.bbq.dto.BbqMenuCategoryResponse;
import com.tastyhouse.external.bbq.dto.BbqMenuResponse;
import com.tastyhouse.external.bbq.dto.BbqMenuSubOptionResponse;
import com.tastyhouse.webapi.crawling.bbq.response.BbqProductCategoryResponse;
import com.tastyhouse.webapi.crawling.bbq.response.BbqProductResponse;
import com.tastyhouse.webapi.crawling.bbq.response.BbqProductSubOptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BBQ 서비스
 *
 * BBQ API 관련 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BbqService {

    private final BbqApiClient bbqApiClient;
    private final ProductCategoryJpaRepository productCategoryJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductOptionGroupJpaRepository productOptionGroupJpaRepository;
    private final ProductOptionJpaRepository productOptionJpaRepository;

    /**
     * BBQ 메뉴 카테고리 목록 조회
     *
     * @return ProductCategory Entity 구조에 맞춘 상품 카테고리 목록
     */
    public List<BbqProductCategoryResponse> getMenuCategories() {
        try {
            List<BbqMenuCategoryResponse> externalCategories = bbqApiClient.getMenuCategoriesSync();
            return externalCategories.stream()
                    .map(this::convertToProductCategoryResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("BBQ 메뉴 카테고리 조회 중 오류 발생", e);
            throw new RuntimeException("BBQ 메뉴 카테고리 조회 실패", e);
        }
    }

    /**
     * BBQ 카테고리별 메뉴 목록 조회
     *
     * @param categoryId 카테고리 ID
     * @return Product Entity 구조에 맞춘 상품 목록
     */
    public List<BbqProductResponse> getMenusByCategoryId(Long categoryId) {
        try {
            List<BbqMenuResponse> externalMenus = bbqApiClient.getMenusByCategoryIdSync(categoryId);
            return externalMenus.stream()
                    .map(this::convertToProductResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("BBQ 카테고리별 메뉴 조회 중 오류 발생: categoryId={}", categoryId, e);
            throw new RuntimeException("BBQ 카테고리별 메뉴 조회 실패", e);
        }
    }

    /**
     * 외부 API 응답을 ProductCategory 구조에 맞는 응답으로 변환
     *
     * @param externalResponse 외부 API 응답
     * @return ProductCategory 구조에 맞춘 응답
     */
    private BbqProductCategoryResponse convertToProductCategoryResponse(BbqMenuCategoryResponse externalResponse) {
        return BbqProductCategoryResponse.builder()
                .id(externalResponse.getId())
                .placeId(null) // 외부 API에는 placeId 정보가 없으므로 null로 설정
                .name(externalResponse.getCategoryName())
                .sort(externalResponse.getPriority())
                .isActive(true) // 외부 API에서 조회된 카테고리는 활성화 상태로 간주
                .build();
    }

    /**
     * BBQ 메뉴 상세 조회
     *
     * @param menuId 메뉴 ID
     * @return Product Entity 구조에 맞춘 상품 상세 정보
     */
    public BbqProductResponse getMenuDetail(Long menuId) {
        try {
            BbqMenuResponse externalMenu = bbqApiClient.getMenuDetailSync(menuId);
            return convertToProductResponse(externalMenu);
        } catch (Exception e) {
            log.error("BBQ 메뉴 상세 조회 중 오류 발생: menuId={}", menuId, e);
            throw new RuntimeException("BBQ 메뉴 상세 조회 실패", e);
        }
    }

    /**
     * 외부 API 응답을 Product 구조에 맞는 응답으로 변환
     *
     * @param externalResponse 외부 API 응답
     * @return Product 구조에 맞춘 응답
     */
    private BbqProductResponse convertToProductResponse(BbqMenuResponse externalResponse) {
        return BbqProductResponse.builder()
                .id(externalResponse.getId())
                .name(externalResponse.getMenuName())
                .description(externalResponse.getDescription())
                .imageUrl(externalResponse.getMenuImageUrl())
                .originalPrice(externalResponse.getMenuPrice())
                .addPrice(externalResponse.getAddPrice())
                .isSoldOut(externalResponse.getIsSoldOut() != null ? externalResponse.getIsSoldOut() : false)
                .isAdultOnly(externalResponse.getIsAdultOnly() != null ? externalResponse.getIsAdultOnly() : false)
                .canDeliver(externalResponse.getCanDeliver() != null ? externalResponse.getCanDeliver() : false)
                .canTakeout(externalResponse.getCanTakeout() != null ? externalResponse.getCanTakeout() : false)
                .build();
    }

    /**
     * BBQ 메뉴 서브 옵션 조회
     *
     * @param menuId 메뉴 ID
     * @return 서브 옵션 목록
     */
    public List<BbqProductSubOptionResponse> getMenuSubOptions(Long menuId) {
        try {
            List<BbqMenuSubOptionResponse> externalSubOptions = bbqApiClient.getMenuSubOptionsSync(menuId);
            return externalSubOptions.stream()
                    .map(this::convertToProductSubOptionResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("BBQ 메뉴 서브 옵션 조회 중 오류 발생: menuId={}", menuId, e);
            throw new RuntimeException("BBQ 메뉴 서브 옵션 조회 실패", e);
        }
    }

    /**
     * 외부 API 응답을 서브 옵션 응답으로 변환
     *
     * @param externalResponse 외부 API 응답
     * @return 서브 옵션 응답
     */
    private BbqProductSubOptionResponse convertToProductSubOptionResponse(BbqMenuSubOptionResponse externalResponse) {
        List<BbqProductSubOptionResponse.SubOptionItemDetailResponse> itemDetails = null;
        if (externalResponse.getSubOptionItemDetailResponseList() != null) {
            itemDetails = externalResponse.getSubOptionItemDetailResponseList().stream()
                    .map(item -> BbqProductSubOptionResponse.SubOptionItemDetailResponse.builder()
                            .id(item.getId())
                            .itemTitle(item.getItemTitle())
                            .addPrice(item.getAddPrice())
                            .isSoldOut(item.getIsSoldOut() != null ? item.getIsSoldOut() : false)
                            .isHidden(item.getIsHidden() != null ? item.getIsHidden() : false)
                            .build())
                    .collect(Collectors.toList());
        }

        return BbqProductSubOptionResponse.builder()
                .id(externalResponse.getId())
                .subOptionTitle(externalResponse.getSubOptionTitle())
                .requiredSelectCount(externalResponse.getRequiredSelectCount())
                .maxSelectCount(externalResponse.getMaxSelectCount())
                .subOptionItemDetailResponseList(itemDetails)
                .build();
    }

    /**
     * 신메뉴 크롤링 및 저장
     *
     * @param placeId 플레이스 ID
     * @return 저장된 상품 정보
     */
    @Transactional
    public Product crawlAndSaveNewMenu(Long placeId) {
        try {
            // 1. PRODUCT_CATEGORY에서 name이 "신메뉴"인 것을 찾기
            List<ProductCategory> categories = productCategoryJpaRepository.findByNameAndPlaceId("신메뉴", placeId);
            if (categories.isEmpty()) {
                throw new RuntimeException("신메뉴 카테고리를 찾을 수 없습니다. placeId: " + placeId);
            }
            ProductCategory newMenuCategory = categories.get(0);
            Long categoryId = newMenuCategory.getId();

            // 2. getMenuCategories 메서드를 호출해서 신메뉴를 찾기
            List<BbqProductCategoryResponse> menuCategories = getMenuCategories();
            BbqProductCategoryResponse newMenuCategoryResponse = menuCategories.stream()
                    .filter(category -> "신메뉴".equals(category.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("BBQ API에서 신메뉴 카테고리를 찾을 수 없습니다."));

            // 3. getMenusByCategoryId 메서드를 호출해서 신메뉴의 상품을 제일 첫번째만 찾기
            List<BbqProductResponse> menus = getMenusByCategoryId(newMenuCategoryResponse.getId());
            if (menus.isEmpty()) {
                throw new RuntimeException("신메뉴에 상품이 없습니다.");
            }
            BbqProductResponse firstMenu = menus.get(0);
            Long menuId = firstMenu.getId();

            // 4. getMenuDetail 메서드를 호출해서 상품의 정보를 Product Entity에 save
            BbqProductResponse menuDetail = getMenuDetail(menuId);
            Product product = Product.builder()
                    .placeId(placeId)
                    .productCategoryId(categoryId)
                    .name(menuDetail.getName())
                    .description(menuDetail.getDescription())
                    .imageUrl(menuDetail.getImageUrl())
                    .originalPrice(menuDetail.getOriginalPrice())
                    .discountPrice(null)
                    .discountRate(null)
                    .rating(null)
                    .reviewCount(0)
                    .isRepresentative(false)
                    .spiciness(null)
                    .isSoldOut(menuDetail.getIsSoldOut() != null ? menuDetail.getIsSoldOut() : false)
                    .isActive(true)
                    .sort(0)
                    .build();
            Product savedProduct = productJpaRepository.save(product);

            // 5. getMenuSubOptions 메서드를 호출해서 ProductOptionGroup, ProductOption Entity에 save
            List<BbqProductSubOptionResponse> subOptions = getMenuSubOptions(menuId);
            for (int i = 0; i < subOptions.size(); i++) {
                BbqProductSubOptionResponse subOption = subOptions.get(i);
                
                // ProductOptionGroup 저장
                ProductOptionGroup optionGroup = ProductOptionGroup.builder()
                        .productId(savedProduct.getId())
                        .name(subOption.getSubOptionTitle())
                        .description(null)
                        .isRequired(subOption.getRequiredSelectCount() != null && subOption.getRequiredSelectCount() > 0)
                        .isMultipleSelect(subOption.getMaxSelectCount() != null && subOption.getMaxSelectCount() > 1)
                        .minSelect(subOption.getRequiredSelectCount())
                        .maxSelect(subOption.getMaxSelectCount())
                        .sort(i)
                        .isActive(true)
                        .build();
                ProductOptionGroup savedOptionGroup = productOptionGroupJpaRepository.save(optionGroup);

                // ProductOption 저장
                if (subOption.getSubOptionItemDetailResponseList() != null) {
                    for (int j = 0; j < subOption.getSubOptionItemDetailResponseList().size(); j++) {
                        BbqProductSubOptionResponse.SubOptionItemDetailResponse itemDetail = 
                                subOption.getSubOptionItemDetailResponseList().get(j);
                        
                        ProductOption productOption = ProductOption.builder()
                                .optionGroupId(savedOptionGroup.getId())
                                .name(itemDetail.getItemTitle())
                                .additionalPrice(itemDetail.getAddPrice() != null ? itemDetail.getAddPrice() : 0)
                                .sort(j)
                                .isSoldOut(itemDetail.getIsSoldOut() != null ? itemDetail.getIsSoldOut() : false)
                                .isActive(!(itemDetail.getIsHidden() != null && itemDetail.getIsHidden()))
                                .build();
                        productOptionJpaRepository.save(productOption);
                    }
                }
            }

            log.info("신메뉴 크롤링 및 저장 완료. placeId: {}, productId: {}", placeId, savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            log.error("신메뉴 크롤링 및 저장 중 오류 발생: placeId={}", placeId, e);
            throw new RuntimeException("신메뉴 크롤링 및 저장 실패", e);
        }
    }
}
