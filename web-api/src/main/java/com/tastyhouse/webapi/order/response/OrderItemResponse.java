package com.tastyhouse.webapi.order.response;

import java.util.List;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        Integer quantity,
        Integer unitPrice,
        Integer discountPrice,
        Integer optionTotalPrice,
        Integer totalPrice,
        boolean isReviewed,
        List<OrderItemOptionResponse> options
) {
}
