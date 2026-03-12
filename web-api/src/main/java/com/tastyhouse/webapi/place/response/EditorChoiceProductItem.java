package com.tastyhouse.webapi.place.response;

import java.math.BigDecimal;

public record EditorChoiceProductItem(
        Long id,
        String placeName,
        String name,
        String imageUrl,
        Integer originalPrice,
        Integer discountPrice,
        BigDecimal discountRate
) {
}
