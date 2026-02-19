package com.tastyhouse.core.repository.payment;

import com.tastyhouse.core.entity.payment.Payment;
import com.tastyhouse.core.entity.payment.PaymentRefund;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.RefundStatus;
import com.tastyhouse.core.entity.payment.dto.OrderListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByPgTid(String pgTid);

    Optional<Payment> findByPgOrderId(String pgOrderId);

    List<Payment> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);

    boolean existsByOrderId(Long orderId);

    List<PaymentRefund> findRefundsByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    Optional<PaymentRefund> findRefundByPgRefundId(String pgRefundId);

    List<PaymentRefund> findRefundsByRefundStatusOrderByCreatedAtDesc(RefundStatus refundStatus);

    Page<OrderListItemDto> findOrderListByMemberId(Long memberId, Pageable pageable);
}
