package com.tastyhouse.core.repository.payment;

import com.tastyhouse.core.entity.payment.dto.MyPaymentListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepository {
    Page<MyPaymentListItemDto> findMyPayments(Long memberId, Pageable pageable);
}
