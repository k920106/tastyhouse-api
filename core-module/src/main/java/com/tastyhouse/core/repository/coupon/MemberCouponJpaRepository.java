package com.tastyhouse.core.repository.coupon;

import com.tastyhouse.core.entity.coupon.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {

    List<MemberCoupon> findByMemberId(Long memberId);

    List<MemberCoupon> findByMemberIdAndIsUsed(Long memberId, Boolean isUsed);

    @Query("SELECT mc FROM MemberCoupon mc WHERE mc.memberId = :memberId AND mc.isUsed = false AND mc.expiredAt > :currentTime")
    List<MemberCoupon> findAvailableCouponsByMemberId(@Param("memberId") Long memberId, @Param("currentTime") LocalDateTime currentTime);

    Optional<MemberCoupon> findByMemberIdAndCouponId(Long memberId, Long couponId);

    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);
}
