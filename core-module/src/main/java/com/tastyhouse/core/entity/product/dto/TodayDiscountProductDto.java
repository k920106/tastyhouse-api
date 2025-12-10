package com.tastyhouse.core.entity.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TodayDiscountProductDto {
    private final Long id;
    private final String placeName;
    private final String name;
    private final String imageUrl;
    private final Integer originalPrice;
    private final Integer discountPrice;
    private final BigDecimal discountRate;

    @QueryProjection
    public TodayDiscountProductDto(Long id, String placeName, String name, String imageUrl,
                                   Integer originalPrice, Integer discountPrice, BigDecimal discountRate) {
        this.id = id;
        this.placeName = placeName;
        this.name = name;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
    }
}