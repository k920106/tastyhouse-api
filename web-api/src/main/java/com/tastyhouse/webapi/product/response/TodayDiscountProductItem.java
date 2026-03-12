package com.tastyhouse.webapi.product.response;

import java.math.BigDecimal;

public record TodayDiscountProductItem(
        Long id,
        String placeName,
        String name,
        String imageUrl,
        Integer originalPrice,
        Integer discountPrice,
        BigDecimal discountRate
) {
}
