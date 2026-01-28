package com.tastyhouse.webapi.payment.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RefundRequest(
    @NotNull(message = "환불 금액은 필수입니다")
    @Min(value = 1, message = "환불 금액은 1원 이상이어야 합니다")
    Integer refundAmount,

    @Size(max = 500, message = "환불 사유는 500자 이내로 입력해주세요")
    String refundReason
) {
}
