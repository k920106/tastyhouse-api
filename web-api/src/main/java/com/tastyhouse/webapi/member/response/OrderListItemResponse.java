package com.tastyhouse.webapi.member.response;

import com.tastyhouse.core.entity.payment.PaymentStatus;
import com.tastyhouse.core.entity.payment.dto.OrderListItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderListItemResponse {
    private Long id;
    private String placeName;
    private String placeThumbnailImageUrl;
    private String firstProductName;
    private Integer totalItemCount;
    private Integer amount;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;

    public static OrderListItemResponse from(OrderListItemDto dto) {
        return OrderListItemResponse.builder()
            .id(dto.getId())
            .placeName(dto.getPlaceName())
            .placeThumbnailImageUrl(dto.getPlaceThumbnailImageUrl())
            .firstProductName(dto.getFirstProductName())
            .totalItemCount(dto.getTotalItemCount())
            .amount(dto.getAmount())
            .paymentStatus(dto.getPaymentStatus())
            .paymentDate(dto.getPaymentDate())
            .build();
    }
}
