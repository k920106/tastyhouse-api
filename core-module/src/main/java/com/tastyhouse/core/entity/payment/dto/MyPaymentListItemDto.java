package com.tastyhouse.core.entity.payment.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.tastyhouse.core.entity.payment.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyPaymentListItemDto {
    private final Long paymentId;
    private final String placeName;
    private final String placeThumbnailImageUrl;
    private final String firstProductName;
    private final Integer totalItemCount;
    private final Integer amount;
    private final PaymentStatus paymentStatus;
    private final LocalDateTime paymentDate;

    @QueryProjection
    public MyPaymentListItemDto(Long paymentId, String placeName,
                                String placeThumbnailImageUrl, String firstProductName,
                                Integer totalItemCount, Integer amount,
                                PaymentStatus paymentStatus, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.placeName = placeName;
        this.placeThumbnailImageUrl = placeThumbnailImageUrl;
        this.firstProductName = firstProductName;
        this.totalItemCount = totalItemCount;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }
}
