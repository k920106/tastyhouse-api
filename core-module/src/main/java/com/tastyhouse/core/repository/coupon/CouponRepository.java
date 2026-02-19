package com.tastyhouse.core.repository.coupon;

import com.tastyhouse.core.entity.coupon.Coupon;
import com.tastyhouse.core.entity.coupon.MemberCoupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    List<Coupon> findActiveCoupons();

    List<Coupon> findIssuableCoupons(LocalDateTime currentTime);

    List<MemberCoupon> findMemberCouponsByMemberId(Long memberId);

    List<MemberCoupon> findMemberCouponsByMemberIdAndIsUsed(Long memberId, Boolean isUsed);

    List<MemberCoupon> findAvailableMemberCouponsByMemberId(Long memberId, LocalDateTime currentTime);

    Optional<MemberCoupon> findMemberCouponByMemberIdAndCouponId(Long memberId, Long couponId);

    boolean existsMemberCouponByMemberIdAndCouponId(Long memberId, Long couponId);
}
