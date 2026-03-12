package com.tastyhouse.webapi.order.response;

public record OrderItemOptionResponse(
        Long id,
        String optionGroupName,
        String optionName,
        Integer additionalPrice
) {
}
