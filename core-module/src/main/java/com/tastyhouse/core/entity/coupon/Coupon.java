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
    name = "COUPON",
    indexes = {
        @Index(name = "idx_coupon_active", columnList = "is_active"),
        @Index(name = "idx_coupon_issue_period", columnList = "issue_start_at, issue_end_at"),
        @Index(name = "idx_coupon_use_period", columnList = "use_start_at, use_end_at")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "min_order_amount", nullable = false)
    private Integer minOrderAmount = 0;

    @Column(name = "max_discount_count")
    private Integer maxDiscountCount;

    @Column(name = "issue_start_at", nullable = false)
    private LocalDateTime issueStartAt;

    @Column(name = "issue_end_at", nullable = false)
    private LocalDateTime issueEndAt;

    @Column(name = "use_start_at", nullable = false)
    private LocalDateTime useStartAt;

    @Column(name = "use_end_at", nullable = false)
    private LocalDateTime useEndAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public Coupon(
        String name,
        String description,
        Integer discountAmount,
        Integer minOrderAmount,
        Integer maxDiscountCount,
        LocalDateTime issueStartAt,
        LocalDateTime issueEndAt,
        LocalDateTime useStartAt,
        LocalDateTime useEndAt,
        Boolean isActive
    ) {
        this.name = name;
        this.description = description;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount != null ? minOrderAmount : 0;
        this.maxDiscountCount = maxDiscountCount;
        this.issueStartAt = issueStartAt;
        this.issueEndAt = issueEndAt;
        this.useStartAt = useStartAt;
        this.useEndAt = useEndAt;
        this.isActive = isActive != null ? isActive : true;
    }
}
