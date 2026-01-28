package com.tastyhouse.core.entity.payment;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "PAYMENT_REFUND")
public class PaymentRefund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private RefundStatus refundStatus;

    @Column(name = "pg_refund_id", length = 100)
    private String pgRefundId;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Builder
    public PaymentRefund(Long paymentId, Integer refundAmount, String refundReason,
                         RefundStatus refundStatus, String pgRefundId, LocalDateTime refundedAt) {
        this.paymentId = paymentId;
        this.refundAmount = refundAmount != null ? refundAmount : 0;
        this.refundReason = refundReason;
        this.refundStatus = refundStatus != null ? refundStatus : RefundStatus.PENDING;
        this.pgRefundId = pgRefundId;
        this.refundedAt = refundedAt;
    }

    public void startProcessing() {
        this.refundStatus = RefundStatus.PROCESSING;
    }

    public void complete(String pgRefundId) {
        this.refundStatus = RefundStatus.COMPLETED;
        this.pgRefundId = pgRefundId;
        this.refundedAt = LocalDateTime.now();
    }

    public void fail() {
        this.refundStatus = RefundStatus.FAILED;
    }
}
