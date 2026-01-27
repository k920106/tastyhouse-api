package com.tastyhouse.webapi.coupon.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class MemberCouponListItemResponse {

    private Long id;
    private Long couponId;
    private String name;
    private String description;
    private Integer discountAmount;
    private Integer minOrderAmount;
    private LocalDateTime useStartAt;
    private LocalDateTime useEndAt;
    private LocalDateTime expiredAt;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private Long daysRemaining;

    public static MemberCouponListItemResponse of(
        Long id,
        Long couponId,
        String name,
        String description,
        Integer discountAmount,
        Integer minOrderAmount,
        LocalDateTime useStartAt,
        LocalDateTime useEndAt,
        LocalDateTime expiredAt,
        Boolean isUsed,
        LocalDateTime usedAt
    ) {
        Long daysRemaining = null;
        if (!isUsed && expiredAt != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(expiredAt)) {
                daysRemaining = ChronoUnit.DAYS.between(now, expiredAt);
            }
        }

        return MemberCouponListItemResponse.builder()
            .id(id)
            .couponId(couponId)
            .name(name)
            .description(description)
            .discountAmount(discountAmount)
            .minOrderAmount(minOrderAmount)
            .useStartAt(useStartAt)
            .useEndAt(useEndAt)
            .expiredAt(expiredAt)
            .isUsed(isUsed)
            .usedAt(usedAt)
            .daysRemaining(daysRemaining)
            .build();
    }
}
