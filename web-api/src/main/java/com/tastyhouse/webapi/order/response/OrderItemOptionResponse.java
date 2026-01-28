package com.tastyhouse.webapi.order.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemOptionResponse {
    private Long id;
    private String optionGroupName;
    private String optionName;
    private Integer additionalPrice;
}
