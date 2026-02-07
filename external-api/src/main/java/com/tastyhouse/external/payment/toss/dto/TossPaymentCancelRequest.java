package com.tastyhouse.external.payment.toss.dto;

import lombok.Builder;

@Builder
public record TossPaymentCancelRequest(
    String cancelReason
) {
}
