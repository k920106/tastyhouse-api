package com.tastyhouse.webapi.place.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceOrderMethodResponse {
    private final Long placeId;
    private final List<OrderMethodItem> orderMethods;

    @Getter
    @Builder
    public static class OrderMethodItem {
        private final String code;
        private final String name;
    }
}
