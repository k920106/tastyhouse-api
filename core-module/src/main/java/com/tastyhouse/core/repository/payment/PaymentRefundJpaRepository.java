package com.tastyhouse.core.repository.payment;

import com.tastyhouse.core.entity.payment.PaymentRefund;
import com.tastyhouse.core.entity.payment.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRefundJpaRepository extends JpaRepository<PaymentRefund, Long> {

    List<PaymentRefund> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    Optional<PaymentRefund> findByPgRefundId(String pgRefundId);

    List<PaymentRefund> findByRefundStatusOrderByCreatedAtDesc(RefundStatus refundStatus);
}
