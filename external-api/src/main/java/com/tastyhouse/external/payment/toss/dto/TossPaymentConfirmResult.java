package com.tastyhouse.external.payment.toss.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TossPaymentConfirmResult {

    private final boolean success;
    private final String paymentKey;
    private final String orderId;
    private final String orderName;
    private final Integer totalAmount;
    private final String status;
    private final LocalDateTime approvedAt;
    private final String receiptUrl;

    // 카드 정보
    private final String cardCompany;
    private final String cardNumber;
    private final Integer installmentPlanMonths;
    private final Boolean isInterestFree;
    private final String cardType;

    // 결제 수단
    private final String method;

    // 에러 정보 (실패 시)
    private final String errorCode;
    private final String errorMessage;
}
