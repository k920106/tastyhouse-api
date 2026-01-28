package com.tastyhouse.webapi.payment.response;

import com.tastyhouse.core.entity.payment.RefundStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentRefundResponse {
    private Long id;
    private Long paymentId;
    private Integer refundAmount;
    private String refundReason;
    private RefundStatus refundStatus;
    private String pgRefundId;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;
}
