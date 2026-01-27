package com.tastyhouse.core.entity.coupon;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
    name = "MEMBER_COUPON",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_coupon",
            columnNames = {"member_id", "coupon_id"}
        )
    },
    indexes = {
        @Index(name = "idx_member_coupon_member_id", columnList = "member_id"),
        @Index(name = "idx_member_coupon_coupon_id", columnList = "coupon_id"),
        @Index(name = "idx_member_coupon_used", columnList = "member_id, is_used")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    public MemberCoupon(
        Long memberId,
        Long couponId,
        Boolean isUsed,
        LocalDateTime usedAt,
        LocalDateTime expiredAt
    ) {
        this.memberId = memberId;
        this.couponId = couponId;
        this.isUsed = isUsed != null ? isUsed : false;
        this.usedAt = usedAt;
        this.expiredAt = expiredAt;
    }

    public void use() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isAvailable() {
        return !isUsed && !isExpired();
    }
}
