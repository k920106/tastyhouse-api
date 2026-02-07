package com.tastyhouse.external.payment.toss.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TossPaymentCancelResult {

    private final boolean success;
    private final String paymentKey;
    private final String orderId;
    private final String orderName;
    private final String status;
    private final Integer totalAmount;
    private final Integer balanceAmount;

    // 취소 정보
    private final String cancelReason;
    private final LocalDateTime canceledAt;
    private final Integer cancelAmount;
    private final Integer refundableAmount;
    private final String cancelStatus;

    // 에러 정보 (실패 시)
    private final String errorCode;
    private final String errorMessage;
}
