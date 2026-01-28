package com.tastyhouse.webapi.payment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentCancelRequest(
    @NotBlank(message = "취소 사유는 필수입니다")
    @Size(max = 500, message = "취소 사유는 500자 이내로 입력해주세요")
    String cancelReason
) {
}
