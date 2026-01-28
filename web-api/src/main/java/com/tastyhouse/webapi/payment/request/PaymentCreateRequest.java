package com.tastyhouse.webapi.payment.request;

import com.tastyhouse.core.entity.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
    @NotNull(message = "주문 ID는 필수입니다")
    Long orderId,

    @NotNull(message = "결제 방법은 필수입니다")
    PaymentMethod paymentMethod
) {
}
