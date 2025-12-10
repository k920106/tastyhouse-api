package com.tastyhouse.webapi.product.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TodayDiscountProductItem {
    private final Long id;
    private final String placeName;
    private final String productName;
    private final String imageUrl;
    private final Integer originalPrice;
    private final Integer discountPrice;
    private final BigDecimal discountRate;
}
