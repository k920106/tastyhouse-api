package com.tastyhouse.external.payment.toss;

import com.tastyhouse.external.payment.toss.dto.TossPaymentCancelRequest;
import com.tastyhouse.external.payment.toss.dto.TossPaymentCancelResult;
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

    public TossPaymentConfirmResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount) {
        TossPaymentConfirmRequest request = TossPaymentConfirmRequest.builder()
            .paymentKey(paymentKey)
            .orderId(pgOrderId)
            .amount(amount)
            .build();

        log.info("토스 결제 승인하기 API 요청. paymentKey: {}, pgOrderId: {}, amount: {}", paymentKey, pgOrderId, amount);

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
                log.warn("토스 결제 승인하기 API 응답 없음. paymentKey: {}", paymentKey);
                TossPaymentConfirmResponse errorResponse = new TossPaymentConfirmResponse();
                errorResponse.setCode("UNKNOWN_ERROR");
                errorResponse.setMessage("응답이 없습니다.");
                return errorResponse;
            }

            log.info("토스 결제 승인하기 API 완료. paymentKey: {}, status: {}", paymentKey, response.getStatus());
            return response;

        } catch (WebClientResponseException e) {
            log.error("토스 결제 승인하기 API 실패. status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            TossPaymentConfirmResponse errorResponse = new TossPaymentConfirmResponse();
            errorResponse.setCode("PG_API_ERROR");
            errorResponse.setMessage("결제 승인하기 API 호출에 실패했습니다: " + e.getMessage());
            return errorResponse;
        } catch (Exception e) {
            log.error("토스 결제 승인하기 API 에러. error: ", e);
            TossPaymentConfirmResponse errorResponse = new TossPaymentConfirmResponse();
            errorResponse.setCode("SYSTEM_ERROR");
            errorResponse.setMessage("결제 승인 중 오류가 발생했습니다: " + e.getMessage());
            return errorResponse;
        }
    }

    public TossPaymentConfirmResult confirmPaymentLegacy(String paymentKey, String pgOrderId, Integer amount) {
        TossPaymentConfirmResponse response = confirmPayment(paymentKey, pgOrderId, amount);

        if (response.isError()) {
            return createErrorResult(response.getCode(), response.getMessage());
        }

        return mapToResult(response);
    }

    public TossPaymentConfirmResponse cancelPayment(String paymentKey, String cancelReason) {
        TossPaymentCancelRequest request = TossPaymentCancelRequest.builder()
            .cancelReason(cancelReason)
            .build();

        String cancelUrl = tossPaymentProperties.getBaseUrl()
            + tossPaymentProperties.getCancelPath().replace("{paymentKey}", paymentKey);

        log.info("토스 전액 취소하기 API 요청. paymentKey: {}, cancelReason: {}", paymentKey, cancelReason);

        try {
            TossPaymentConfirmResponse response = webClientBuilder.build()
                .post()
                .uri(cancelUrl)
                .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .block();

            if (response == null) {
                log.warn("토스 전액 취소하기 API 응답 없음. paymentKey: {}", paymentKey);
                TossPaymentConfirmResponse errorResponse = new TossPaymentConfirmResponse();
                errorResponse.setCode("UNKNOWN_ERROR");
                errorResponse.setMessage("응답이 없습니다.");
                return errorResponse;
            }

            log.info("토스 전액 취소하기 API 완료. paymentKey: {}, status: {}", paymentKey, response.getStatus());
            return response;
        } catch (WebClientResponseException e) {
            log.error("토스 전액 취소하기 API 실패. status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            TossPaymentConfirmResponse errorResponse = new TossPaymentConfirmResponse();
            errorResponse.setCode("PG_API_ERROR");
            errorResponse.setMessage("결제 취소 API 호출에 실패했습니다: " + e.getMessage());
            return errorResponse;
        } catch (Exception e) {
            log.error("토스 전액 취소하기 API 에러. error: ", e);
            TossPaymentConfirmResponse errorResponse = new TossPaymentConfirmResponse();
            errorResponse.setCode("SYSTEM_ERROR");
            errorResponse.setMessage("결제 취소 중 오류가 발생했습니다: " + e.getMessage());
            return errorResponse;
        }
    }

    public TossPaymentCancelResult cancelPaymentWithResult(String paymentKey, String cancelReason) {
        TossPaymentConfirmResponse response = cancelPayment(paymentKey, cancelReason);

        if (response.isError()) {
            return TossPaymentCancelResult.builder()
                .success(false)
                .errorCode(response.getCode())
                .errorMessage(response.getMessage())
                .build();
        }

        return mapToCancelResult(response);
    }

    private TossPaymentCancelResult mapToCancelResult(TossPaymentConfirmResponse response) {
        TossPaymentCancelResult.TossPaymentCancelResultBuilder builder = TossPaymentCancelResult.builder()
            .success(true)
            .paymentKey(response.getPaymentKey())
            .orderId(response.getOrderId())
            .orderName(response.getOrderName())
            .status(response.getStatus())
            .totalAmount(response.getTotalAmount())
            .balanceAmount(response.getBalanceAmount());

        if (response.getCancels() != null && !response.getCancels().isEmpty()) {
            TossPaymentConfirmResponse.Cancel latestCancel = response.getCancels().get(0);
            builder.cancelReason(latestCancel.getCancelReason())
                .canceledAt(parseDateTime(latestCancel.getCanceledAt()))
                .cancelAmount(latestCancel.getCancelAmount())
                .refundableAmount(latestCancel.getRefundableAmount())
                .cancelStatus(latestCancel.getCancelStatus());
        }

        return builder.build();
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
