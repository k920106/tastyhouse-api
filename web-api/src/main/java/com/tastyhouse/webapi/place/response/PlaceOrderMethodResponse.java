package com.tastyhouse.webapi.place.response;

import java.util.List;

public record PlaceOrderMethodResponse(
        Long placeId,
        List<OrderMethodItem> orderMethods
) {
    public record OrderMethodItem(
            String code,
            String name
    ) {
    }
}
