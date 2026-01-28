package com.tastyhouse.webapi.payment.response;

import com.tastyhouse.core.entity.payment.PaymentMethod;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.PgProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Integer amount;
    private PgProvider pgProvider;
    private String pgTid;
    private String pgOrderId;
    private String cardCompany;
    private String cardNumber;
    private Integer installmentMonths;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;
    private String receiptUrl;
    private String cashReceiptNumber;
    private String cashReceiptType;
    private LocalDateTime createdAt;
}
