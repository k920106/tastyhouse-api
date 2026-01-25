package com.tastyhouse.external.bbq;

import com.tastyhouse.external.bbq.dto.BbqMenuCategoryResponse;
import com.tastyhouse.external.bbq.dto.BbqMenuResponse;
import com.tastyhouse.external.bbq.dto.BbqMenuSubOptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * BBQ API 클라이언트
 *
 * BBQ 외부 API를 호출하는 클라이언트입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BbqApiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${bbq.api.base-url:https://bbq.co.kr}")
    private String baseUrl;

    private WebClient getWebClient() {
        return webClientBuilder.build();
    }

    /**
     * BBQ 메뉴 카테고리 목록 조회
     *
     * @return 메뉴 카테고리 목록
     */
    public Mono<List<BbqMenuCategoryResponse>> getMenuCategories() {
        String url = baseUrl + "/api/delivery/menu/category";

        return getWebClient().get()
                .uri(url)
                .retrieve()
                .bodyToFlux(BbqMenuCategoryResponse.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .doOnSuccess(categories -> {
                    log.info("BBQ 메뉴 카테고리 조회 성공: {}개", categories.size());
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("BBQ 메뉴 카테고리 조회 실패: Status={}, Message={}", 
                            ex.getStatusCode(), ex.getMessage());
                })
                .doOnError(Throwable.class, ex -> {
                    if (!(ex instanceof WebClientResponseException)) {
                        log.error("BBQ 메뉴 카테고리 조회 중 예외 발생", ex);
                    }
                });
    }

    /**
     * BBQ 메뉴 카테고리 목록 조회 (동기 방식)
     *
     * @return 메뉴 카테고리 목록
     */
    public List<BbqMenuCategoryResponse> getMenuCategoriesSync() {
        return getMenuCategories()
                .block(Duration.ofSeconds(10));
    }

    /**
     * BBQ 카테고리별 메뉴 목록 조회
     *
     * @param categoryId 카테고리 ID
     * @return 메뉴 목록
     */
    public Mono<List<BbqMenuResponse>> getMenusByCategoryId(Long categoryId) {
        String url = baseUrl + "/api/delivery/menu/" + categoryId;

        return getWebClient().get()
                .uri(url)
                .retrieve()
                .bodyToFlux(BbqMenuResponse.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .doOnSuccess(menus -> {
                    log.info("BBQ 카테고리별 메뉴 조회 성공: categoryId={}, 메뉴 수={}", categoryId, menus.size());
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("BBQ 카테고리별 메뉴 조회 실패: categoryId={}, Status={}, Message={}", 
                            categoryId, ex.getStatusCode(), ex.getMessage());
                })
                .doOnError(Throwable.class, ex -> {
                    if (!(ex instanceof WebClientResponseException)) {
                        log.error("BBQ 카테고리별 메뉴 조회 중 예외 발생: categoryId={}", categoryId, ex);
                    }
                });
    }

    /**
     * BBQ 카테고리별 메뉴 목록 조회 (동기 방식)
     *
     * @param categoryId 카테고리 ID
     * @return 메뉴 목록
     */
    public List<BbqMenuResponse> getMenusByCategoryIdSync(Long categoryId) {
        return getMenusByCategoryId(categoryId)
                .block(Duration.ofSeconds(10));
    }

    /**
     * BBQ 메뉴 상세 조회
     *
     * @param menuId 메뉴 ID
     * @return 메뉴 상세 정보
     */
    public Mono<BbqMenuResponse> getMenuDetail(Long menuId) {
        String url = baseUrl + "/api/delivery/menu/detail/" + menuId;

        return getWebClient().get()
                .uri(url)
                .retrieve()
                .bodyToMono(BbqMenuResponse.class)
                .timeout(Duration.ofSeconds(10))
                .doOnSuccess(menu -> {
                    log.info("BBQ 메뉴 상세 조회 성공: menuId={}, menuName={}", menuId, menu.getMenuName());
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("BBQ 메뉴 상세 조회 실패: menuId={}, Status={}, Message={}", 
                            menuId, ex.getStatusCode(), ex.getMessage());
                })
                .doOnError(Throwable.class, ex -> {
                    if (!(ex instanceof WebClientResponseException)) {
                        log.error("BBQ 메뉴 상세 조회 중 예외 발생: menuId={}", menuId, ex);
                    }
                });
    }

    /**
     * BBQ 메뉴 상세 조회 (동기 방식)
     *
     * @param menuId 메뉴 ID
     * @return 메뉴 상세 정보
     */
    public BbqMenuResponse getMenuDetailSync(Long menuId) {
        return getMenuDetail(menuId)
                .block(Duration.ofSeconds(10));
    }

    /**
     * BBQ 메뉴 서브 옵션 조회
     *
     * @param menuId 메뉴 ID
     * @return 메뉴 서브 옵션 목록
     */
    public Mono<List<BbqMenuSubOptionResponse>> getMenuSubOptions(Long menuId) {
        String url = baseUrl + "/api/delivery/menu/sub-option/" + menuId;

        return getWebClient().get()
                .uri(url)
                .retrieve()
                .bodyToFlux(BbqMenuSubOptionResponse.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .doOnSuccess(subOptions -> {
                    log.info("BBQ 메뉴 서브 옵션 조회 성공: menuId={}, 옵션 수={}", menuId, subOptions.size());
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("BBQ 메뉴 서브 옵션 조회 실패: menuId={}, Status={}, Message={}", 
                            menuId, ex.getStatusCode(), ex.getMessage());
                })
                .doOnError(Throwable.class, ex -> {
                    if (!(ex instanceof WebClientResponseException)) {
                        log.error("BBQ 메뉴 서브 옵션 조회 중 예외 발생: menuId={}", menuId, ex);
                    }
                });
    }

    /**
     * BBQ 메뉴 서브 옵션 조회 (동기 방식)
     *
     * @param menuId 메뉴 ID
     * @return 메뉴 서브 옵션 목록
     */
    public List<BbqMenuSubOptionResponse> getMenuSubOptionsSync(Long menuId) {
        return getMenuSubOptions(menuId)
                .block(Duration.ofSeconds(10));
    }
}
