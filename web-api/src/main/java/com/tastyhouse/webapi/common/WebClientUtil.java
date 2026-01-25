package com.tastyhouse.webapi.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * WebClient 유틸리티 클래스
 *
 * 외부 API 호출을 위한 편의 메서드를 제공합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientUtil {

    private final WebClient webClient;

    /**
     * GET 요청
     *
     * @param url 요청 URL
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> get(String url, Class<T> responseType) {
        return get(url, null, null, responseType);
    }

    /**
     * GET 요청 (헤더 포함)
     *
     * @param url 요청 URL
     * @param headers 헤더 맵
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> get(String url, Map<String, String> headers, Class<T> responseType) {
        return get(url, headers, null, responseType);
    }

    /**
     * GET 요청 (헤더 및 쿼리 파라미터 포함)
     *
     * @param url 요청 URL
     * @param headers 헤더 맵
     * @param queryParams 쿼리 파라미터 맵
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> get(String url, Map<String, String> headers, Map<String, String> queryParams, Class<T> responseType) {
        WebClient.RequestHeadersSpec<?> spec = webClient.get()
                .uri(uriBuilder -> {
                    if (queryParams != null && !queryParams.isEmpty()) {
                        queryParams.forEach(uriBuilder::queryParam);
                    }
                    return uriBuilder.path(url).build();
                });

        if (headers != null && !headers.isEmpty()) {
            spec = spec.headers(httpHeaders -> headers.forEach(httpHeaders::add));
        }

        return spec
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(10))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("WebClient GET 요청 실패: URL={}, Status={}, Message={}", 
                            url, ex.getStatusCode(), ex.getMessage());
                })
                .doOnSuccess(result -> {
                    log.debug("WebClient GET 요청 성공: URL={}", url);
                });
    }

    /**
     * POST 요청
     *
     * @param url 요청 URL
     * @param requestBody 요청 본문
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> post(String url, Object requestBody, Class<T> responseType) {
        return post(url, null, requestBody, responseType);
    }

    /**
     * POST 요청 (헤더 포함)
     *
     * @param url 요청 URL
     * @param headers 헤더 맵
     * @param requestBody 요청 본문
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> post(String url, Map<String, String> headers, Object requestBody, Class<T> responseType) {
        WebClient.RequestBodySpec spec = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON);

        if (headers != null && !headers.isEmpty()) {
            spec = spec.headers(httpHeaders -> headers.forEach(httpHeaders::add));
        }

        return spec
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(10))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("WebClient POST 요청 실패: URL={}, Status={}, Message={}", 
                            url, ex.getStatusCode(), ex.getMessage());
                })
                .doOnSuccess(result -> {
                    log.debug("WebClient POST 요청 성공: URL={}", url);
                });
    }

    /**
     * PUT 요청
     *
     * @param url 요청 URL
     * @param requestBody 요청 본문
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> put(String url, Object requestBody, Class<T> responseType) {
        return put(url, null, requestBody, responseType);
    }

    /**
     * PUT 요청 (헤더 포함)
     *
     * @param url 요청 URL
     * @param headers 헤더 맵
     * @param requestBody 요청 본문
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> put(String url, Map<String, String> headers, Object requestBody, Class<T> responseType) {
        WebClient.RequestBodySpec spec = webClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON);

        if (headers != null && !headers.isEmpty()) {
            spec = spec.headers(httpHeaders -> headers.forEach(httpHeaders::add));
        }

        return spec
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(10))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("WebClient PUT 요청 실패: URL={}, Status={}, Message={}", 
                            url, ex.getStatusCode(), ex.getMessage());
                })
                .doOnSuccess(result -> {
                    log.debug("WebClient PUT 요청 성공: URL={}", url);
                });
    }

    /**
     * DELETE 요청
     *
     * @param url 요청 URL
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> delete(String url, Class<T> responseType) {
        return delete(url, null, responseType);
    }

    /**
     * DELETE 요청 (헤더 포함)
     *
     * @param url 요청 URL
     * @param headers 헤더 맵
     * @param responseType 응답 타입
     * @return Mono<T>
     */
    public <T> Mono<T> delete(String url, Map<String, String> headers, Class<T> responseType) {
        WebClient.RequestHeadersSpec<?> spec = webClient.delete()
                .uri(url);

        if (headers != null && !headers.isEmpty()) {
            spec = spec.headers(httpHeaders -> headers.forEach(httpHeaders::add));
        }

        return spec
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(10))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("WebClient DELETE 요청 실패: URL={}, Status={}, Message={}", 
                            url, ex.getStatusCode(), ex.getMessage());
                })
                .doOnSuccess(result -> {
                    log.debug("WebClient DELETE 요청 성공: URL={}", url);
                });
    }
}
