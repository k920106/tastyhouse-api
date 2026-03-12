package com.tastyhouse.webapi.payment.response;

import com.tastyhouse.core.entity.payment.PaymentMethod;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.PgProvider;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        Integer amount,
        PgProvider pgProvider,
        String pgTid,
        String pgOrderId,
        String cardCompany,
        String cardNumber,
        Integer installmentMonths,
        LocalDateTime approvedAt,
        LocalDateTime cancelledAt,
        String cancelReason,
        String receiptUrl,
        String cashReceiptNumber,
        String cashReceiptType,
        LocalDateTime createdAt
) {
}
