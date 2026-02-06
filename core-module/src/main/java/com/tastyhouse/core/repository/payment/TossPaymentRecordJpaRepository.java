package com.tastyhouse.core.repository.payment;

import com.tastyhouse.core.entity.payment.TossPaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TossPaymentRecordJpaRepository extends JpaRepository<TossPaymentRecord, Long> {

    Optional<TossPaymentRecord> findByPaymentId(Long paymentId);

    Optional<TossPaymentRecord> findByPaymentKey(String paymentKey);

    Optional<TossPaymentRecord> findByOrderId(String orderId);
}
