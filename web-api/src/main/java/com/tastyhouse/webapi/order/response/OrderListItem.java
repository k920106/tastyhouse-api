package com.tastyhouse.webapi.order.response;

import com.tastyhouse.core.entity.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderListItem {
    private Long id;
    private String orderNumber;
    private OrderStatus orderStatus;
    private String placeName;
    private String firstProductName;
    private String firstProductImageUrl;
    private Integer totalProductCount;
    private Integer finalAmount;
    private LocalDateTime createdAt;
}
