package com.tastyhouse.webapi.payment.response;

import com.tastyhouse.core.entity.payment.RefundStatus;

import java.time.LocalDateTime;

public record PaymentRefundResponse(
        Long id,
        Long paymentId,
        Integer refundAmount,
        String refundReason,
        RefundStatus refundStatus,
        String pgRefundId,
        LocalDateTime refundedAt,
        LocalDateTime createdAt
) {
}
