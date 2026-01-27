package com.tastyhouse.core.repository.coupon;

import com.tastyhouse.core.entity.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByIsActiveTrue();

    List<Coupon> findByIsActiveTrueAndIssueStartAtLessThanEqualAndIssueEndAtGreaterThanEqual(
        LocalDateTime currentTime1,
        LocalDateTime currentTime2
    );
}
