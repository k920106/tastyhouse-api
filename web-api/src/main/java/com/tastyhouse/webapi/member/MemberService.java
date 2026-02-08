package com.tastyhouse.webapi.member;

import com.tastyhouse.core.entity.rank.RankType;
import com.tastyhouse.core.repository.member.MemberJpaRepository;
import com.tastyhouse.core.repository.point.MemberPointJpaRepository;
import com.tastyhouse.core.repository.rank.MemberReviewRankJpaRepository;
import com.tastyhouse.webapi.coupon.CouponService;
import com.tastyhouse.webapi.coupon.response.MemberCouponListItemResponse;
import com.tastyhouse.webapi.member.response.MemberContactResponse;
import com.tastyhouse.webapi.member.response.MemberProfileResponse;
import com.tastyhouse.webapi.member.response.PointResponse;
import com.tastyhouse.webapi.member.response.UsablePointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberPointJpaRepository memberPointJpaRepository;
    private final MemberReviewRankJpaRepository memberReviewRankJpaRepository;
    private final CouponService couponService;

    public PointResponse getMemberPoint(Long memberId) {
        return memberPointJpaRepository.findByMemberId(memberId)
            .map(PointResponse::from)
            .orElseGet(() -> PointResponse.builder()
                .availablePoints(0)
                .expiredThisMonth(0)
                .build());
    }

    public Optional<MemberContactResponse> getMemberContact(Long memberId) {
        return memberJpaRepository.findById(memberId)
            .map(member -> new MemberContactResponse(
                member.getFullName(),
                member.getPhoneNumber(),
                member.getUsername()
            ));
    }

    public List<MemberCouponListItemResponse> getMemberCoupons(Long memberId) {
        return couponService.getMemberCoupons(memberId);
    }

    public List<MemberCouponListItemResponse> getAvailableMemberCoupons(Long memberId) {
        return couponService.getAvailableMemberCoupons(memberId);
    }

    public UsablePointResponse getUsablePoint(Long memberId) {
        return memberPointJpaRepository.findByMemberId(memberId)
            .map(UsablePointResponse::from)
            .orElseGet(() -> UsablePointResponse.builder()
                .usablePoints(0)
                .build());
    }

    public Optional<MemberProfileResponse> getMemberProfile(Long memberId) {
        return memberJpaRepository.findById(memberId)
            .map(member -> {
                // 전체 리뷰 개수 조회 (ALL 타입)
                Integer reviewCount = memberReviewRankJpaRepository
                    .findByMemberIdAndRankTypeAndBaseDate(
                        memberId,
                        RankType.ALL,
                        LocalDate.now()
                    )
                    .map(rank -> rank.getReviewCount())
                    .orElse(0);

                return MemberProfileResponse.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .grade(member.getMemberGrade())
                    .reviewCount(reviewCount)
                    .statusMessage(member.getStatusMessage())
                    .profileImageUrl(member.getProfileImageUrl())
                    .build();
            });
    }

    @Transactional
    public void updateMemberProfile(Long memberId, String nickname, String statusMessage, String profileImageUrl) {
        memberJpaRepository.findById(memberId).ifPresent(member -> {
            if (nickname != null) {
                member.setNickname(nickname);
            }
            if (statusMessage != null) {
                member.setStatusMessage(statusMessage);
            }
            if (profileImageUrl != null) {
                member.setProfileImageUrl(profileImageUrl);
            }
        });
    }
}
