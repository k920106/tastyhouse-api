package com.tastyhouse.webapi.order.response;

import com.tastyhouse.core.entity.payment.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        PaymentStatus paymentStatus,
        String placeName,
        String placePhoneNumber,
        String ordererName,
        String ordererPhone,
        String ordererEmail,
        Integer totalProductAmount,
        Integer productDiscountAmount,
        Integer couponDiscountAmount,
        Integer pointDiscountAmount,
        Integer totalDiscountAmount,
        Integer finalAmount,
        Integer usedPoint,
        Integer earnedPoint,
        List<OrderItemResponse> orderItems,
        PaymentSummaryResponse payment,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
}
