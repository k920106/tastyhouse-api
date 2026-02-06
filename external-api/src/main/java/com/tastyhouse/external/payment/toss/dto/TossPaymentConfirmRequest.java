package com.tastyhouse.external.payment.toss.dto;

import lombok.Builder;

@Builder
public record TossPaymentConfirmRequest(
    String paymentKey,
    Integer amount,
    String orderId
) {
}
