package com.tastyhouse.webapi.order.response;

import com.tastyhouse.core.entity.payment.PaymentMethod;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentSummaryResponse {
    private Long id;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Integer amount;
    private LocalDateTime approvedAt;
    private String receiptUrl;
}
