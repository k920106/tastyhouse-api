package com.tastyhouse.webapi.crawling.bbq;

import com.tastyhouse.external.bbq.BbqApiClient;
import com.tastyhouse.external.bbq.dto.BbqMenuCategoryResponse;
import com.tastyhouse.webapi.crawling.bbq.response.BbqProductCategoryResponse;
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
}
