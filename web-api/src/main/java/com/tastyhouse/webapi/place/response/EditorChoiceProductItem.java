package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class EditorChoiceProductItem {
    private final Long id;
    private final String placeName;
    private final String name;
    private final String imageUrl;
    private final Integer originalPrice;
    private final Integer discountPrice;
    private final BigDecimal discountRate;
}
