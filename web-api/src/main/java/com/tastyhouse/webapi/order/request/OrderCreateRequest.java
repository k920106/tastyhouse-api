package com.tastyhouse.webapi.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
    @NotNull(message = "매장 ID는 필수입니다")
    Long placeId,

    @NotEmpty(message = "주문 상품은 필수입니다")
    @Valid
    List<OrderItemRequest> orderItems,

    Long memberCouponId,

    @NotNull(message = "포인트 사용 금액은 필수입니다")
    @Min(value = 0, message = "포인트 사용 금액은 0 이상이어야 합니다")
    Integer usePoint,

    @NotNull(message = "상품 금액은 필수입니다")
    @Min(value = 0, message = "상품 금액은 0 이상이어야 합니다")
    Integer totalProductAmount,

    @NotNull(message = "할인 금액은 필수입니다")
    @Min(value = 0, message = "할인 금액은 0 이상이어야 합니다")
    Integer totalDiscountAmount,

    @NotNull(message = "상품 할인 금액은 필수입니다")
    @Min(value = 0, message = "상품 할인 금액은 0 이상이어야 합니다")
    Integer productDiscountAmount,

    @NotNull(message = "쿠폰 사용 금액은 필수입니다")
    @Min(value = 0, message = "쿠폰 사용 금액은 0 이상이어야 합니다")
    Integer couponDiscountAmount,

    @NotNull(message = "결제 금액은 필수입니다")
    @Min(value = 0, message = "결제 금액은 0 이상이어야 합니다")
    Integer finalAmount
) {
}
