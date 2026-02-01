package com.tastyhouse.webapi.coupon;

import com.tastyhouse.core.entity.coupon.Coupon;
import com.tastyhouse.core.entity.coupon.MemberCoupon;
import com.tastyhouse.core.service.CouponCoreService;
import com.tastyhouse.webapi.coupon.response.MemberCouponListItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponCoreService couponCoreService;

    public List<MemberCouponListItemResponse> getMemberCoupons(Long memberId) {
        List<MemberCoupon> memberCoupons = couponCoreService.findMemberCoupons(memberId);

        if (memberCoupons.isEmpty()) {
            return List.of();
        }

        // 쿠폰 ID 목록 추출
        List<Long> couponIds = memberCoupons.stream()
            .map(MemberCoupon::getCouponId)
            .distinct()
            .toList();

        // 쿠폰 정보 조회
        Map<Long, Coupon> couponMap = couponIds.stream()
            .map(couponCoreService::findCouponById)
            .filter(opt -> opt.isPresent())
            .map(opt -> opt.get())
            .collect(Collectors.toMap(Coupon::getId, coupon -> coupon));

        // 응답 생성
        return memberCoupons.stream()
            .map(memberCoupon -> {
                Coupon coupon = couponMap.get(memberCoupon.getCouponId());
                if (coupon == null) {
                    return null;
                }

                return MemberCouponListItemResponse.of(
                    memberCoupon.getId(),
                    coupon.getId(),
                    coupon.getName(),
                    coupon.getDescription(),
                    coupon.getDiscountType(),
                    coupon.getDiscountAmount(),
                    coupon.getMaxDiscountAmount(),
                    coupon.getMinOrderAmount(),
                    coupon.getUseStartAt(),
                    coupon.getUseEndAt(),
                    memberCoupon.getExpiredAt(),
                    memberCoupon.getIsUsed(),
                    memberCoupon.getUsedAt()
                );
            })
            .filter(item -> item != null)
            .toList();
    }

    public List<MemberCouponListItemResponse> getAvailableMemberCoupons(Long memberId) {
        List<MemberCoupon> memberCoupons = couponCoreService.findAvailableMemberCoupons(memberId);

        if (memberCoupons.isEmpty()) {
            return List.of();
        }

        // 쿠폰 ID 목록 추출
        List<Long> couponIds = memberCoupons.stream()
            .map(MemberCoupon::getCouponId)
            .distinct()
            .toList();

        // 쿠폰 정보 조회
        Map<Long, Coupon> couponMap = couponIds.stream()
            .map(couponCoreService::findCouponById)
            .filter(opt -> opt.isPresent())
            .map(opt -> opt.get())
            .collect(Collectors.toMap(Coupon::getId, coupon -> coupon));

        // 응답 생성
        return memberCoupons.stream()
            .map(memberCoupon -> {
                Coupon coupon = couponMap.get(memberCoupon.getCouponId());
                if (coupon == null) {
                    return null;
                }

                return MemberCouponListItemResponse.of(
                    memberCoupon.getId(),
                    coupon.getId(),
                    coupon.getName(),
                    coupon.getDescription(),
                    coupon.getDiscountType(),
                    coupon.getDiscountAmount(),
                    coupon.getMaxDiscountAmount(),
                    coupon.getMinOrderAmount(),
                    coupon.getUseStartAt(),
                    coupon.getUseEndAt(),
                    memberCoupon.getExpiredAt(),
                    memberCoupon.getIsUsed(),
                    memberCoupon.getUsedAt()
                );
            })
            .filter(item -> item != null)
            .toList();
    }
}
