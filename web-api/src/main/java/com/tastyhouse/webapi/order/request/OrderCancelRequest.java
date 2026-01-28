package com.tastyhouse.webapi.order.request;

import jakarta.validation.constraints.Size;

public record OrderCancelRequest(
    @Size(max = 500, message = "취소 사유는 500자 이내로 입력해주세요")
    String cancelReason
) {
}
