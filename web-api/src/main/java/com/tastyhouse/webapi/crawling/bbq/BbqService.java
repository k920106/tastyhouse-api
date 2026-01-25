package com.tastyhouse.webapi.crawling.bbq;

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
}
