package com.tastyhouse.external.payment.toss;

import com.tastyhouse.external.payment.toss.dto.TossPaymentConfirmRequest;
import com.tastyhouse.external.payment.toss.dto.TossPaymentConfirmResponse;
import com.tastyhouse.external.payment.toss.dto.TossPaymentConfirmResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final WebClient.Builder webClientBuilder;
    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentConfirmResult confirmPayment(String paymentKey, String orderId, Integer amount) {
        TossPaymentConfirmRequest request = TossPaymentConfirmRequest.builder()
            .paymentKey(paymentKey)
            .orderId(orderId)
            .amount(amount)
            .build();

        try {
            TossPaymentConfirmResponse response = webClientBuilder.build()
                .post()
                .uri(tossPaymentProperties.getBaseUrl() + tossPaymentProperties.getConfirmPath())
                .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .block();

            if (response == null) {
                return createErrorResult("UNKNOWN_ERROR", "응답이 없습니다.");
            }

            if (response.isError()) {
                return createErrorResult(response.getCode(), response.getMessage());
            }

            return mapToResult(response);

        } catch (WebClientResponseException e) {
            log.error("Toss payment confirm failed. status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return createErrorResult("PG_API_ERROR", "결제 승인 API 호출에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Toss payment confirm error", e);
            return createErrorResult("SYSTEM_ERROR", "결제 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String createAuthorizationHeader() {
        String credentials = tossPaymentProperties.getSecretKey() + ":";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }

    private TossPaymentConfirmResult mapToResult(TossPaymentConfirmResponse response) {
        TossPaymentConfirmResult.TossPaymentConfirmResultBuilder builder = TossPaymentConfirmResult.builder()
            .success(response.isSuccess())
            .paymentKey(response.getPaymentKey())
            .orderId(response.getOrderId())
            .orderName(response.getOrderName())
            .totalAmount(response.getTotalAmount())
            .status(response.getStatus())
            .approvedAt(parseDateTime(response.getApprovedAt()))
            .method(response.getMethod());

        if (response.getReceipt() != null) {
            builder.receiptUrl(response.getReceipt().getUrl());
        }

        if (response.getCard() != null) {
            TossPaymentConfirmResponse.Card card = response.getCard();
            builder.cardCompany(mapIssuerCodeToCardCompany(card.getIssuerCode()))
                .cardNumber(card.getNumber())
                .installmentPlanMonths(card.getInstallmentPlanMonths())
                .isInterestFree(card.getIsInterestFree())
                .cardType(card.getCardType());
        }

        return builder.build();
    }

    private TossPaymentConfirmResult createErrorResult(String errorCode, String errorMessage) {
        return TossPaymentConfirmResult.builder()
            .success(false)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .build();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return offsetDateTime.toLocalDateTime();
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr, e);
            return null;
        }
    }

    private String mapIssuerCodeToCardCompany(String issuerCode) {
        if (issuerCode == null) {
            return null;
        }
        return switch (issuerCode) {
            case "3K" -> "기업BC";
            case "46" -> "광주";
            case "71" -> "롯데";
            case "30" -> "KDB산업";
            case "31" -> "BC";
            case "51" -> "삼성";
            case "38" -> "새마을";
            case "41" -> "신한";
            case "62" -> "신협";
            case "36" -> "씨티";
            case "33" -> "우리";
            case "37" -> "우체국";
            case "39" -> "저축";
            case "35" -> "전북";
            case "42" -> "제주";
            case "15" -> "카카오뱅크";
            case "3A" -> "케이뱅크";
            case "24" -> "토스뱅크";
            case "21" -> "하나";
            case "61" -> "현대";
            case "11" -> "KB국민";
            case "91" -> "NH농협";
            case "34" -> "Sh수협";
            default -> issuerCode;
        };
    }
}
