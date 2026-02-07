package com.tastyhouse.webapi.order.response;

import com.tastyhouse.core.entity.payment.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private PaymentStatus paymentStatus;
    private String placeName;
    private String placePhoneNumber;
    private String ordererName;
    private String ordererPhone;
    private String ordererEmail;
    private Integer totalProductAmount;
    private Integer productDiscountAmount;
    private Integer couponDiscountAmount;
    private Integer pointDiscountAmount;
    private Integer totalDiscountAmount;
    private Integer finalAmount;
    private Integer usedPoint;
    private Integer earnedPoint;
    private List<OrderItemResponse> orderItems;
    private PaymentSummaryResponse payment;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
