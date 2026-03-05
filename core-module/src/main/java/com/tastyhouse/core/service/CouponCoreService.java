package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.coupon.Coupon;
import com.tastyhouse.core.entity.coupon.MemberCoupon;
import com.tastyhouse.core.repository.coupon.CouponJpaRepository;
import com.tastyhouse.core.repository.coupon.MemberCouponJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCoreService {

    private final CouponJpaRepository couponJpaRepository;
    private final MemberCouponJpaRepository memberCouponJpaRepository;

    @Transactional(readOnly = true)
    public Optional<Coupon> findCouponById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Transactional(readOnly = true)
    public List<Coupon> findActiveCoupons() {
        return couponJpaRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Coupon> findIssuableCoupons(LocalDateTime currentTime) {
        return couponJpaRepository.findByIsActiveTrueAndIssueStartAtLessThanEqualAndIssueEndAtGreaterThanEqual(
            currentTime, currentTime
        );
    }

    @Transactional(readOnly = true)
    public List<MemberCoupon> findMemberCoupons(Long memberId) {
        return memberCouponJpaRepository.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<MemberCoupon> findAvailableMemberCoupons(Long memberId) {
        return memberCouponJpaRepository.findAvailableCouponsByMemberId(memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Optional<MemberCoupon> findMemberCouponById(Long memberCouponId) {
        return memberCouponJpaRepository.findById(memberCouponId);
    }

    @Transactional(readOnly = true)
    public Optional<MemberCoupon> findMemberCoupon(Long memberId, Long couponId) {
        return memberCouponJpaRepository.findByMemberIdAndCouponId(memberId, couponId);
    }

    @Transactional(readOnly = true)
    public boolean existsMemberCoupon(Long memberId, Long couponId) {
        return memberCouponJpaRepository.existsByMemberIdAndCouponId(memberId, couponId);
    }

    @Transactional
    public Coupon saveCoupon(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Transactional
    public MemberCoupon saveMemberCoupon(MemberCoupon memberCoupon) {
        return memberCouponJpaRepository.save(memberCoupon);
    }

    @Transactional
    public void deleteMemberCoupon(Long memberCouponId) {
        memberCouponJpaRepository.deleteById(memberCouponId);
    }
}
