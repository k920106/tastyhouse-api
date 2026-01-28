package com.tastyhouse.webapi.order.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private Integer unitPrice;
    private Integer discountPrice;
    private Integer optionTotalPrice;
    private Integer totalPrice;
    private List<OrderItemOptionResponse> options;
}
