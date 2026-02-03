package com.tastyhouse.webapi.order.request;

import jakarta.validation.constraints.NotNull;

public record OrderItemOptionRequest(
    @NotNull(message = "옵션 그룹 ID는 필수입니다")
    Long groupId,

    @NotNull(message = "옵션 ID는 필수입니다")
    Long optionId
) {
}
